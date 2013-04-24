/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Version	: 3.1.1
 * Author	: Bruce Liang
 * Porject	: https://code.google.com/p/portal-basic
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

package com.bruce.mvc;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bruce.tag.Message;
import com.bruce.util.http.HttpHelper;

/**
 * 
 * {@link Action} 公共基类。
 *
 */
public class ActionSupport implements Action, ActionContextAware, Validateable, AsyncContextAware
{
	private ServletContext servletContext;
	private HttpServletRequest request;
	private HttpServletResponse response;

	/** 默认 {@link Action} 入口方法（返回 {@link Action#SUCCESS}） */
	public String execute() throws Exception
	{
		return SUCCESS;
	}
	
	/** {@link Validateable#validate()} 的默认实现 */
	@Override
	public boolean validate()
	{
		return true;
	}

	/** 获取 {@link HttpServletRequest} 的指定属性值 */
	public final <T> T getRequestAttribute(String name)
	{
		return HttpHelper.getRequestAttribute(request, name);
	}

	/** 设置 {@link HttpServletRequest} 的指定属性值 */
	public final <T> void setRequestAttribute(String name, T value)
	{
		HttpHelper.setRequestAttribute(request, name, value);
	}
	
	/** 删除 {@link HttpServletRequest} 的指定属性值 */
	public final void removeRequestAttribute(String name)
	{
		HttpHelper.removeRequestAttribute(request, name);
	}

	/** 获取 {@link HttpSession} 的指定属性值 */
	public final <T> T getSessionAttribute(String name)
	{
		HttpSession session = getSession(false);
		
		if(session != null)
			return HttpHelper.getSessionAttribute(session, name);
		
		return null;
	}

	/** 设置 {@link HttpSession} 的指定属性值 */
	public final <T> void setSessionAttribute(String name, T value)
	{
		HttpSession session = getSession(true);
		HttpHelper.setSessionAttribute(session, name, value);
	}
	
	/** 删除 {@link HttpSession} 的指定属性值 */
	public final void removeSessionAttribute(String name)
	{
		HttpSession session = getSession(false);
		
		if(session != null)
			HttpHelper.removeSessionAttribute(session, name);
	}
	
	/** 销毁 {@link HttpSession} */
	public final void invalidateSession()
	{
		HttpSession session = getSession(false);
		
		if(session != null)
			HttpHelper.invalidateSession(session);
	}

	/** 获取 {@link ServletContext} 的指定属性值 */
	public final <T> T getApplicationAttribute(String name)
	{
		return HttpHelper.getApplicationAttribute(servletContext, name);
	}

	/** 设置 {@link ServletContext} 的指定属性值 */
	public final <T> void setApplicationAttribute(String name, T value)
	{
		HttpHelper.setApplicationAttribute(servletContext, name, value);
	}
	
	/** 删除 {@link ServletContext} 的指定属性值 */
	public final void removeApplicationAttribute(String name)
	{
		HttpHelper.removeApplicationAttribute(servletContext, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，并去除前后空格 */
	public final String getParam(String name)
	{
		return HttpHelper.getParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值 */
	public final String getParamNoTrim(String name)
	{
		return HttpHelper.getParamNoTrim(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的参数名称集合 */
	public final List<String> getParamNames()
	{
		return HttpHelper.getParamNames(request);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值集合 */
	public final List<String> getParamValues(String name)
	{
		return HttpHelper.getParamValues(request, name);
	}
	
	/** 获取 {@link HttpServletRequest} 的所有参数名称和值 */
	public final Map<String, String[]> getParamMap()
	{
		return HttpHelper.getParamMap(request);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Boolean getBooleanParam(String name)
	{
		return HttpHelper.getBooleanParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final boolean getBooleanParam(String name, boolean def)
	{
		return HttpHelper.getBooleanParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Byte getByteParam(String name)
	{
		return HttpHelper.getByteParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final byte getByteParam(String name, byte def)
	{
		return HttpHelper.getByteParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Character getCharParam(String name)
	{
		return HttpHelper.getCharParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final char getCharParam(String name, char def)
	{
		return HttpHelper.getCharParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Short getShortParam(String name)
	{
		return HttpHelper.getShortParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final short getShortParam(String name, short def)
	{
		return HttpHelper.getShortParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Integer getIntParam(String name)
	{
		return HttpHelper.getIntParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final int getIntParam(String name, int def)
	{
		return HttpHelper.getIntParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Long getLongParam(String name)
	{
		return HttpHelper.getLongParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final long getLongParam(String name, long def)
	{
		return HttpHelper.getLongParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Float getFloatParam(String name)
	{
		return HttpHelper.getFloatParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final float getFloatParam(String name, float def)
	{
		return HttpHelper.getFloatParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Double getDoubleParam(String name)
	{
		return HttpHelper.getDoubleParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final double getDoubleParam(String name, double def)
	{
		return HttpHelper.getDoubleParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Date getDateParam(String name)
	{
		return HttpHelper.getDateParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Date getDateParam(String name, String format)
	{
		return HttpHelper.getDateParam(request, name, format);
	}

	/** 使用表单元素创建 Form Bean (表单元素的名称和 Form Bean 属性或成员变量名完全一致) */
	public final <T> T createFormBean(Class<T> clazz)
	{
		return HttpHelper.createFormBean(request, clazz);
	}

	/** 使用表单元素创建 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性或成员变量) */
	public final <T> T createFormBean(Class<T> clazz, Map<String, String> keyMap)
	{
		return HttpHelper.createFormBean(request, clazz, keyMap);
	}
	
	/** 使用表单元素创建 Form Bean (表单元素的名称和 Form Bean 属性名完全一致) */
	public final <T> T createFormBeanByProperties(Class<T> clazz)
	{
		return HttpHelper.createFormBeanByProperties(request, clazz);
	}

	/** 使用表单元素创建 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性) */
	public final <T> T createFormBeanByProperties(Class<T> clazz, Map<String, String> keyMap)
	{
		return HttpHelper.createFormBeanByProperties(request, clazz, keyMap);
	}
	
	/** 使用表单元素创建 Form Bean (表单元素的名称和 Form Bean 成员变量名完全一致) */
	public final <T> T createFormBeanByFieldValues(Class<T> clazz)
	{
		return HttpHelper.createFormBeanByFieldValues(request, clazz);
	}

	/** 使用表单元素创建 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 成员变量) */
	public final <T> T createFormBeanByFieldValues(Class<T> clazz, Map<String, String> keyMap)
	{
		return HttpHelper.createFormBeanByFieldValues(request, clazz, keyMap);
	}
	
	/** 使用表单元素填充 Form Bean (表单元素的名称和 Form Bean 属性名完全一致) */
	public final <T> void fillFormBeanProperties(T bean)
	{
		HttpHelper.fillFormBeanProperties(request, bean);
	}
	
	/** 使用表单元素填充 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性) */
	public final <T> void fillFormBeanProperties(T bean, Map<String, String> keyMap)
	{
        HttpHelper.fillFormBeanProperties(request, bean, keyMap);
	}
	
	/** 使用表单元素填充 Form Bean (表单元素的名称和 Form Bean 成员变量名完全一致) */
	public final <T> void fillFormBeanFieldValues(T bean)
	{
		HttpHelper.fillFormBeanFieldValues(request, bean);
	}
	
	/** 使用表单元素填充 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 成员变量) */
	public final <T> void fillFormBeanFieldValues(T bean, Map<String, String> keyMap)
	{
        HttpHelper.fillFormBeanFieldValues(request, bean, keyMap);
	}

	/** 使用表单元素填充 Form Bean (表单元素的名称和 Form Bean 属性或成员变量名完全一致) */
	public final <T> void fillFormBeanPropertiesOrFieldValues(T bean)
	{
		HttpHelper.fillFormBeanPropertiesOrFieldValues(request, bean);
	}
	
	/** 使用表单元素填充 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性或成员变量) */
	public final <T> void fillFormBeanPropertiesOrFieldValues(T bean, Map<String, String> keyMap)
	{
		HttpHelper.fillFormBeanPropertiesOrFieldValues(request, bean, keyMap);
	}

	/** 获取所有 {@link Cookie} */
	public final Cookie[] getCookies()
	{
		return HttpHelper.getCookies(request);
	}
	
	/** 获取指定名称的 {@link Cookie} */
	public final Cookie getCookie(String name)
	{
		return HttpHelper.getCookie(request, name);
	}

	/** 获取指定名称的 {@link Cookie} 值，失败返回 null */
	public final String getCookieValue(String name)
	{
		return HttpHelper.getCookieValue(request, name);
	}

	/** 添加 {@link Cookie} */
	public final void addCookie(Cookie cookie)
	{
		HttpHelper.addCookie(response, cookie);
	}

	/** 添加 {@link Cookie} */
	public final void addCookie(String name, String value)
	{
		HttpHelper.addCookie(response, name, value);
	}

	/** 获取当前 {@link HttpSession} 的 {@link Locale} 属性 */
	public final Locale getLocale()
	{
		Locale locale = (Locale)getSessionAttribute(Message.SESSION_LOCALE_KEY);

		if(locale == null)
			locale = Locale.getDefault();

		return locale;
	}

	/** 设置当前 {@link HttpSession} 的 {@link Locale} 属性 */
	public final void setLocale(Locale locale)
	{
		setSessionAttribute(Message.SESSION_LOCALE_KEY, locale);
	}
	
	/** 获取 URL 地址在文件系统的绝对路径,
	 * 
	 * Servlet 2.4 以上通过 request.getServletContext().getRealPath() 获取,
	 * Servlet 2.4 以下通过 request.getRealPath() 获取。
	 *  
	 */
	public final String getRequestRealPath(String path)
	{
		return HttpHelper.getRequestRealPath(request, path);
	}
	
	/** 获取 URL 的  BASE 路径 */
	public final String getRequestBasePath()
	{
		return HttpHelper.getRequestBasePath(request);
	}

	/** 禁止浏览器缓存当前页面 */
	public final void setNoCacheHeader()
	{
		HttpHelper.setNoCacheHeader(response);
	}
	
	/** 获取 {@link ServletContext} */
	public final ServletContext getServletContext()
	{
		return servletContext;
	}

	/** 设置 {@link ServletContext} */
	@Override
	public void setServletContext(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}

	/** 获取 {@link HttpServletRequest} */
	public final HttpServletRequest getRequest()
	{
		return request;
	}

	/** 设置 {@link HttpServletRequest} */
	@Override
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	/** 获取 {@link HttpServletResponse} */
	public final HttpServletResponse getResponse()
	{
		return response;
	}

	/** 设置 {@link HttpServletResponse} */
	@Override
	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	/** 检查 {@link HttpSession} 对象是否存在 */
	public final boolean isSessionExist()
	{
		return getSession(false) != null;
	}

	/** 获取 {@link HttpSession} 对象，如果没有则进行创建。 */
	public final HttpSession getSession()
	{
		return getSession(true);
	}

	/** 获取 {@link HttpSession} 对象，如果没有则根据参数决定是否创建。 */
	public final HttpSession getSession(boolean create)
	{
		return HttpHelper.getSession(request, create);
	}

	/** 创建 {@link HttpSession} 对象，如果已存在则返回原对象。 */
	public final HttpSession createSession()
	{
		return getSession();
	}
	
	/* **************************************************************************************************** */
	//										Async Action Support											//
	/* **************************************************************************************************** */

	/** 预定义异步 Action Result */
	public static final String ASYNC_COMPLETE	= COMPLETE;
	/** 预定义异步 Action Result */
	public static final String ASYNC_TIMEOUT	= TIMEOUT;
	/** 预定义异步 Action Result */
	public static final String ASYNC_ERROR		= ERROR;

	private ActionDispatcher dispatcher;
	private ActionDispatcher.ActionPackage pkg;
	private ActionDispatcher.ActionEntryConfig aec;
	
	/** 参考：{@link AsyncContextAware#setActionDispatcher(ActionDispatcher)} */
	@Override
	public void setActionDispatcher(ActionDispatcher dispatcher)
	{
		this.dispatcher = dispatcher;
	}
	
	/** 参考：{@link AsyncContextAware#setActionPackage(ActionPackage)} */
	@Override
	public void setActionPackage(ActionDispatcher.ActionPackage pkg)
	{
		this.pkg = pkg;
	}

	/** 参考：{@link AsyncContextAware#setActionEntryConfig(ActionEntryConfig)} */
	@Override
	public void setActionEntryConfig(ActionDispatcher.ActionEntryConfig aec)
	{
		this.aec = aec;
	}
	
	/** 预定义异步 Action 任务返回值：{@link ActionSupport#ASYNC_COMPLETE} */
	@SuppressWarnings("unchecked")
	protected <T> T getAsyncCompleteResult()
	{
		return (T)ASYNC_COMPLETE;
	}
	
	/** 预定义异步 Action 任务返回值：{@link ActionSupport#ASYNC_TIMEOUT} <br>
	 * 
	 * 异步 Action 任务执行超时（发生 {@link AsyncListener#onTimeout(AsyncEvent)} 事件），
	 * 应用框架通过该方法获取超时情形下的 Result，该 Result 为 Action 最终处理结果 
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getAsyncTimeoutResult()
	{
		return (T)ASYNC_TIMEOUT;
	}
	
	/** 预定义异步 Action 任务返回值：{@link ActionSupport#ASYNC_ERROR} <br>
	 * 
	 * 异步 Action 任务执行出错（发生 {@link AsyncListener#onError(AsyncEvent)} 事件），
	 * 应用框架通过该方法获取出错情形下的 Result，该 Result 为 Action 最终处理结果 
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getAsyncErrorResult()
	{
		return (T)ASYNC_ERROR;
	}
	
	/** 检测 {@link AsyncTask} 的实际类型是否为 {@link ActionTask} 的子类 <br>
	 * 如果不是 {@link ActionTask} 的子类则抛出 {@link IllegalArgumentException} 异常 <br>
	 * 
	 * 该方法在  {@link ActionSupport#startAsync(AsyncTask)} 内部调用
	 * 
	 */
	protected void checkTaskType(AsyncTask task)
	{
		if(!(task instanceof ActionTask))
			throw new IllegalArgumentException("the 'tast' arg must be subclass of " + ActionTask.class.getName());
	}

	/** 处理异步任务返回的 Result */
	protected <T> void dispatchResult(HttpServletRequest request, HttpServletResponse response, T result) throws ServletException, IOException
	{
		dispatcher.dispatchResult(request, response, pkg, aec, this, (String)result);
	}
	
	/** 启动异步任务（使用系统默认超时值），参考：{@linkplain ActionSupport#startAsync(AsyncTask, long, AsyncTaskLauncher, AsyncListener...)}
	 * 
	 *  @param task			: 异步任务对象
	 *  @param listeners	: 异步任务监听器，用于监听异步任务执行状态。
	 *  @return				  void
	 *  @since				  Servlet 3.0
	 */
	protected void startAsync(AsyncTask task, AsyncListener ... listeners)
	{
		startAsync(task, -1, (AsyncTaskLauncher)null, listeners);
	}
	
	/** 启动异步任务，参考：{@linkplain ActionSupport#startAsync(AsyncTask, long, AsyncTaskLauncher, AsyncListener...)}
	 * 
	 *  @param task			: 异步任务对象<br>
	 *  @param timeout		: 任务超时值（毫秒），应用框架会以 {@linkplain ActionSupport#getAsyncTimeoutResult() 
	 *  					  getAsyncTimeoutResult()} 得到的值作为 Action 的最终处理结果，并立刻返回到客户端。此时，
	 *  					  {@linkplain AsyncTask} 的 {@linkplain AsyncTask#run() run()} 方法仍然继续执行，但会忽略其结果。
	 *  					  <ul>
	 *  						<li><b>小于 0：</b>使用 Web 容器的默认超时值</li>
	 *  						<li><b>等于 0：</b>永不超时</li>
	 *  						<li><b>大于 0：</b>使用设定的超时值</li>
	 *  					  </ul>
	 *  @param listeners	: 异步任务监听器，用于监听异步任务执行状态。
	 *  @return				  void
	 *  @since				  Servlet 3.0
	 */
	protected void startAsync(AsyncTask task, long timeout, AsyncListener ... listeners)
	{
		startAsync(task, timeout, (AsyncTaskLauncher)null, listeners);
	}
	
	/** 启动异步任务（使用系统默认超时值），参考：{@linkplain ActionSupport#startAsync(AsyncTask, long, AsyncTaskLauncher, AsyncListener...)}
	 * 
	 *  @param task			: 异步任务对象<br>
	 *  @param launcher		: 异步任务启动器，如果为 <b>null</b> 则使用容器内置的线程池启动异步任务。
	 *  @param listeners	: 异步任务监听器，用于监听异步任务执行状态。
	 *  @return				  void
	 *  @since				  Servlet 3.0
	 */
	protected void startAsync(AsyncTask task, AsyncTaskLauncher launcher, AsyncListener ... listeners)
	{
		startAsync(task, -1, launcher, listeners);
	}
	
	/** 启动异步任务（永不超时），参考：{@linkplain ActionSupport#startAsync(AsyncTask, long, AsyncTaskLauncher, AsyncListener...)}
	 * 
	 *  @param task			: 异步任务对象
	 *  @param listeners	: 异步任务监听器，用于监听异步任务执行状态。
	 *  @return				  void
	 *  @since				  Servlet 3.0
	 */
	protected void startAsyncNoTimeout(AsyncTask task, AsyncListener ... listeners)
	{
		startAsync(task, 0, (AsyncTaskLauncher)null, listeners);
	}
	
	/** 启动异步任务（永不超时），参考：{@linkplain ActionSupport#startAsync(AsyncTask, long, AsyncTaskLauncher, AsyncListener...)}
	 * 
	 *  @param task			: 异步任务对象
	 *  @param launcher		: 异步任务启动器，如果为 <b>null</b> 则使用容器内置的线程池启动异步任务。
	 *  @param listeners	: 异步任务监听器，用于监听异步任务执行状态。
	 *  @return				  void
	 *  @since				  Servlet 3.0
	 */
	protected void startAsyncNoTimeout(AsyncTask task, AsyncTaskLauncher launcher, AsyncListener ... listeners)
	{
		startAsync(task, 0, launcher, listeners);
	}
	
	/** 启动异步任务
	 * 
	 *  @param task			: 异步任务对象
	 *  @param timeout		: 任务超时值（毫秒），应用框架会以 {@linkplain ActionSupport#getAsyncTimeoutResult() 
	 *  					  getAsyncTimeoutResult()} 得到的值作为 Action 的最终处理结果，并立刻返回到客户端。此时，
	 *  					  {@linkplain AsyncTask} 的 {@linkplain AsyncTask#run() run()} 方法仍然继续执行，但会忽略其结果。
	 *  					  <ul>
	 *  						<li><b>小于 0：</b>使用 Web 容器的默认超时值</li>
	 *  						<li><b>等于 0：</b>永不超时</li>
	 *  						<li><b>大于 0：</b>使用设定的超时值</li>
	 *  					  </ul>
	 *  @param launcher		: 异步任务启动器，如果为 <b>null</b> 则使用容器内置的线程池启动异步任务。
	 *  @param listeners	: 异步任务监听器，用于监听异步任务执行状态。
	 *  @return				  void
	 *  @since				  Servlet 3.0
	 */
	protected void startAsync(AsyncTask task, long timeout, AsyncTaskLauncher launcher, AsyncListener ... listeners)
	{
		HttpServletRequest request		= getRequest();
		HttpServletResponse response	= getResponse();
		
		if(!request.isAsyncSupported())
			throw new UnsupportedOperationException("async operation is not supported by current request");
		
		checkTaskType(task);
		
		AsyncContext ctx = request.startAsync(request, response);
		
		if(timeout >= 0)
			ctx.setTimeout(timeout);
		
		if(listeners != null)
		{
			for(AsyncListener listener : listeners)
				ctx.addListener(listener, request, response);
		}
		
		Runnable runner = new AsyncTaskRunner(ctx, task);
		
		if(launcher == null)
			ctx.start(runner);
		else
			launcher.start(runner);
	}
	
	private class AsyncTaskRunner implements Runnable
	{
		private AsyncContext ctx;
		private AsyncTask task;
		private InternalAsyncListener listener = new InternalAsyncListener();
		
		private AsyncTaskRunner(AsyncContext ctx, AsyncTask task)
		{
			this.ctx	= ctx;
			this.task	= task;
			
			task.setAsyncContext(ctx);
			ctx.addListener(listener);
		}

		@Override
		public void run()
		{
			try
			{
				Object result = task.run();
				
				synchronized(listener)
				{				
					listener.setFlag(InternalAsyncListener.COMPLETE_FLAG);
					
					if(!listener.hasTimeout() && !listener.hasError())
					{
						HttpServletRequest request		= (HttpServletRequest)ctx.getRequest();
						HttpServletResponse response	= (HttpServletResponse)ctx.getResponse();

						dispatchResult(request, response, result);
					}
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				try
				{
					synchronized(listener)
					{
						listener.setFlag(InternalAsyncListener.COMPLETE_FLAG);
						
						if(!listener.hasTimeout() && !listener.hasError())
							ctx.complete();		
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private class InternalAsyncListener implements AsyncListener
	{
		private static final byte START_ASYNC_FLAG	= 0x01;
		private static final byte COMPLETE_FLAG		= 0x02;
		private static final byte TIMEOUT_FLAG		= 0x04;
		private static final byte ERROR_FLAG		= 0x08;
		
		private byte flag;
		
		@SuppressWarnings("unused")
		private boolean hasStartAsync()	{return (flag & START_ASYNC_FLAG)	!= 0;}
		private boolean hasComplete()	{return (flag & COMPLETE_FLAG)		!= 0;}
		private boolean hasTimeout()	{return (flag & TIMEOUT_FLAG)		!= 0;}
		private boolean hasError()		{return (flag & ERROR_FLAG)			!= 0;}
		
		@Override
		synchronized public void onComplete(AsyncEvent evt) throws IOException
		{
			setFlag(COMPLETE_FLAG);
		}

		@Override
		synchronized public void onError(AsyncEvent evt) throws IOException
		{
			setFlag(ERROR_FLAG);
			
			if(!hasComplete() && !hasTimeout())
			{
				Object result = getAsyncErrorResult();
				dispatch(evt, result);
			}
		}

		@Override
		synchronized public void onStartAsync(AsyncEvent evt) throws IOException
		{
			setFlag(START_ASYNC_FLAG);
		}

		@Override
		synchronized public void onTimeout(AsyncEvent evt) throws IOException
		{
			setFlag(TIMEOUT_FLAG);
			
			if(!hasComplete() && !hasError())
			{
				Object result = getAsyncTimeoutResult();
				dispatch(evt, result);
			}
		}
		
		private void setFlag(byte f)
		{
			flag |= f;
		}
		
		private void dispatch(AsyncEvent evt, Object result)
		{
			HttpServletRequest request		= (HttpServletRequest)evt.getAsyncContext().getRequest();
			HttpServletResponse response	= (HttpServletResponse)evt.getAsyncContext().getResponse();
			
			try
			{
				dispatchResult(request, response, result);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}	
	}
}
