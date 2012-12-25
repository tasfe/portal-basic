/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Author	: Bruce Liang
 * Bolg		: http://www.cnblogs.com/ldcsaa
 * WeiBo	: http://weibo.com/u/1402935851
 * QQ Group	: http://qun.qq.com/#jointhegroup/gid/75375912
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bruce.ext.rest;

import static com.bruce.ext.rest.RequestType.GET;
import static com.bruce.ext.rest.RequestType.POST;
import static com.bruce.ext.rest.Rest.Constant.REQ_ATTR_REST_CONTEXT;
import static com.bruce.ext.rest.Rest.Constant.REQ_PARAM_REST_METHOD;
import static com.bruce.ext.rest.renderer.RenderType.HTML;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bruce.ext.rest.renderer.RenderType;
import com.bruce.util.CryptHelper;
import com.bruce.util.GeneralHelper;
import com.bruce.util.http.HttpHelper;

/**
* 
* REST 前端控制器，实现为 {@link Filter}
*
*/
public class RestDispatcher implements Filter
{
	private static final String PATH_SEPARATOR					= HttpHelper.URL_PATH_SEPARATOR;
	private static final String SUFFIX_CHARACTER				= ".";
	
	private static final RenderType DEFAULT_RENDER_TYPE			= HTML;
	private static final String DEFAULT_ACTION_SUFFIX			= ".action";
	private static final String DEFAULT_REST_BASE_PATH			= PATH_SEPARATOR;
	private static final String DEFAULT_DEF_ACTION_PATH			= PATH_SEPARATOR;
	private static final String DEFAULT_SUPPORT_RENDER_TYPES	= DEFAULT_RENDER_TYPE.toString();
	
	private static final String GLOBAL_KEY						= "global";
	private static final String ENCODING_KEY					= "encoding";
	private static final String ACTION_SUFFIX_KEY				= "action-suffix";
	private static final String REST_BASE_PATH_KEY				= "rest-base-path";
	private static final String DEFAULT_ACTION_PATH_KEY			= "default-action-path";
	private static final String SUPPORT_RENDER_TYPES_KEY		= "support-render-types";
	private static final String ENTITIS_KEY						= "entities";
	private static final String ENTITIS_ACTION_PATH_KEY			= "action-path";
	private static final String ENTITY_KEY						= "entity";
	private static final String ENTITY_NAME_KEY					= "name";
	private static final String ENTITY_ACTION_KEY				= "action";
	
	private static final String CONFIG_FILE_KEY					= "rest-config-file";
	private static final String DEFAULT_CONFIG_FILE				= "rest-config.xml";
	
	private static final String REQ_TYPE_GET	= GET.toString();
	private static final String REQ_TYPE_POST	= POST.toString();

	private static final Map<String, RequestType> SUPPORTED_REQ_TYPES_MAP	= new HashMap<String, RequestType>();
	private static final Map<String, RenderType> SUPPORTED_RENDER_TYPES_MAP	= new HashMap<String, RenderType>();
	
	static
	{
		for(RequestType type : RequestType.values())
			SUPPORTED_REQ_TYPES_MAP.put(type.toString(), type);
		
		for(RenderType type : RenderType.values())
			SUPPORTED_RENDER_TYPES_MAP.put(type.toString(), type);
	}
	
	private int contextPathLength;
	
	private String encoding				 = null;
	private String actionSuffix			 = DEFAULT_ACTION_SUFFIX;
	private String restBasePath			 = DEFAULT_REST_BASE_PATH;
	private String defaultActionPath	 = DEFAULT_DEF_ACTION_PATH;
	private RenderType defaultRenderType = DEFAULT_RENDER_TYPE;
	
	private Map<String, RenderType> supportRenderTypes	= new HashMap<String, RenderType>();
	private Map<String, ActionInfo> entityMap			= new HashMap<String, RestDispatcher.ActionInfo>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		contextPathLength	= filterConfig.getServletContext().getContextPath().length();
		String confFile		= filterConfig.getInitParameter(CONFIG_FILE_KEY);
		
		if(GeneralHelper.isStrEmpty(confFile))
			confFile = DEFAULT_CONFIG_FILE;
		
		confFile = GeneralHelper.getClassResourcePath(RestDispatcher.class, confFile);
		
		loadConfig(confFile);
	}

	private void loadConfig(String confFile) throws ServletException
	{
		try
		{
            SAXReader sr	= new SAXReader();
            Document doc	= sr.read(new File(confFile));
            Element root	= doc.getRootElement();
            
            parseGlobal(root);
        	parseEntities(root);
		}
		catch(Exception e)
		{
			throw new ServletException("load REST config fail", e);
		}
	}
	
	private void parseGlobal(Element root)
	{
		Element global = root.element(GLOBAL_KEY);
		if(global != null)
		{
			Element enc = global.element(ENCODING_KEY);
			if(enc != null)
			{
				String str = enc.getTextTrim();
				if(GeneralHelper.isStrNotEmpty(str))
					encoding = str;
			}
			
			Element acSuffix = global.element(ACTION_SUFFIX_KEY);
			if(acSuffix != null)
			{
				String str = acSuffix.getTextTrim();
				if(GeneralHelper.isStrNotEmpty(str))
				{
					if(str.startsWith(SUFFIX_CHARACTER))
						actionSuffix = str;
					else
						actionSuffix = SUFFIX_CHARACTER + str;
				}
			}
			
			Element rbPath = global.element(REST_BASE_PATH_KEY);
			if(rbPath != null)
			{
				String str = rbPath.getTextTrim();
				if(GeneralHelper.isStrNotEmpty(str))
					restBasePath = HttpHelper.ensurePath(str, DEFAULT_REST_BASE_PATH);
			}
			
			Element defAcPath = global.element(DEFAULT_ACTION_PATH_KEY);
			if(defAcPath != null)
			{
				String str = defAcPath.getTextTrim();
				if(GeneralHelper.isStrNotEmpty(str))
					defaultActionPath = HttpHelper.ensurePath(str, DEFAULT_DEF_ACTION_PATH);
			}
		}
		
		parseSupportRenderTypes(global);
	}

	private void parseSupportRenderTypes(Element global)
	{
		String types = DEFAULT_SUPPORT_RENDER_TYPES;
		
		if(global != null)
		{
    		Element srTypes	= global.element(SUPPORT_RENDER_TYPES_KEY);
    		if(srTypes != null)
    		{
    			String str = srTypes.getTextTrim();
    			if(GeneralHelper.isStrNotEmpty(str))
    				types = str;
    		}
		}
		
		String[] ts = GeneralHelper.splitStr(types);
		
		for(int i = 0; i < ts.length; i++)
		{
			String type   = ts[i];
			RenderType rt = SUPPORTED_RENDER_TYPES_MAP.get(type);
			
			if(rt == null)
			{
				String msg = String.format(	"parse '%s' fail, only support %s",
											SUPPORT_RENDER_TYPES_KEY, SUPPORTED_RENDER_TYPES_MAP.keySet());
				throw new RestException(msg);
			}
			
			if(i == 0) defaultRenderType = rt;
			supportRenderTypes.put(type, rt);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseEntities(Element root)
	{
		List<Element> entsList = root.elements(ENTITIS_KEY);
		for(Element entities : entsList)
		{
			String path	= entities.attributeValue(ENTITIS_ACTION_PATH_KEY);
			path		= HttpHelper.ensurePath(path, PATH_SEPARATOR);
			
			List<Element> entList = entities.elements(ENTITY_KEY);
			
			for(Element entity : entList)
			{
				String name = entity.attributeValue(ENTITY_NAME_KEY);
				
				if(GeneralHelper.isStrNotEmpty(name) && !entityMap.containsKey(name))
				{
					String action = entity.attributeValue(ENTITY_ACTION_KEY);
					
					if(GeneralHelper.isStrEmpty(action))
						action = name;
					
					entityMap.put(name, new ActionInfo(path, action));
				}
			}
		}	
	}

	@Override
	public void destroy()
	{
		entityMap			= null;
		supportRenderTypes	= null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request	 = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		if(encoding != null)
		{
    		request.setCharacterEncoding(encoding);
    		response.setCharacterEncoding(encoding);
		}

		String reqPath = request.getRequestURI();
		reqPath = CryptHelper.urlDecode(reqPath, encoding);
		reqPath = reqPath.substring(contextPathLength);

		if(!process(request, response, reqPath))
			chain.doFilter(request, response);
	}

	private boolean process(HttpServletRequest request, HttpServletResponse response, String reqPath) throws ServletException, IOException
	{	
		if(reqPath.startsWith(restBasePath))
		{
			int begin = restBasePath.length();
			int index = parseEntityEndIndex(reqPath, begin);
			
			if(index > begin)
			{
				RestContext context = new RestContext();
				
				if(!parseRequestPathAndRenderType(reqPath, index, context))
					return false;
				
				if(!parseRequestType(request, context))
					return false;
				
				String actionPath = parseActionPath(reqPath, begin, index);
				
				request.setAttribute(REQ_ATTR_REST_CONTEXT, context);
				request.getRequestDispatcher(actionPath).forward(request, response);
				
				return true;
			}
		}

		return false;
	}

	private int parseEntityEndIndex(String reqPath, int begin)
	{
		int index = reqPath.indexOf(PATH_SEPARATOR, begin);
		
		if(index == -1)
		{
			index = reqPath.lastIndexOf(SUFFIX_CHARACTER);
			
			if(index == -1)
				index = reqPath.length();
		}
		
		return index;
	}

	private boolean parseRequestPathAndRenderType(String reqPath, int index, RestContext context)
	{
		String suffix	= null;
		int pos1		= reqPath.lastIndexOf(SUFFIX_CHARACTER);
		
		if(pos1 == -1)
			context.requestPath = reqPath.substring(index);
		else
		{
			int pos2 = reqPath.lastIndexOf(PATH_SEPARATOR);
			
			if(pos1 < pos2)
				context.requestPath = reqPath.substring(index);
			else
			{
				context.requestPath	= reqPath.substring(index, pos1);
				suffix				= reqPath.substring(pos1 + 1);
			}
		}
				
		RenderType type = (suffix == null) ? defaultRenderType : supportRenderTypes.get(suffix);
		
		if(type != null)
		{
			context.renderType = type;
			return true;
		}
		
		return false;
	}

	private boolean parseRequestType(HttpServletRequest request, RestContext context)
	{
		String method = request.getMethod();
		
		if(method.equals(REQ_TYPE_GET) || method.equals(REQ_TYPE_POST))
		{
			String restMethod = request.getParameter(REQ_PARAM_REST_METHOD);
			
			if(restMethod != null)
			{
				method = restMethod.toUpperCase();
				if(!SUPPORTED_REQ_TYPES_MAP.containsKey(method))
				{
					String msg = String.format("request param '%s' contains illegal value '%s'",
												REQ_PARAM_REST_METHOD, restMethod); 
					throw new RestException(msg);
				}
			}
		}
		
		RequestType type = SUPPORTED_REQ_TYPES_MAP.get(method);
		
		if(type != null)
		{
			context.requestType = type;
			return true;
		}
		
		return false;
	}

	private String parseActionPath(String reqPath, int begin, int index)
	{
		String entity = reqPath.substring(begin, index);
		
		ActionInfo info = entityMap.get(entity);
		if(info == null) info = new ActionInfo(defaultActionPath, entity);
		
		return info + actionSuffix;
	}

	private static class ActionInfo
	{
		String path;
		String name;
		
		ActionInfo(String path, String name)
		{
			this.path = path;
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return HttpHelper.ensurePath(path, PATH_SEPARATOR) + name;
		}
	}
}
