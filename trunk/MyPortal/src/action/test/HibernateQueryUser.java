package action.test;

import java.util.List;

import vo.User;

import com.bruce.mvc.ActionSupport;
import com.bruce.mvc.FormBean;
import com.bruce.util.basedao.FacadeProxy;

import dao.hbn.UserDao;

@FormBean
public class HibernateQueryUser extends ActionSupport
{
	private String name;
	private int experience;
	private List<User> users;
	
	@Override
	public String execute() throws Exception
	{
		UserDao dao = FacadeProxy.getAutoCommitProxy(UserDao.class);
		users = dao.findUsers(name, experience);
		
		return SUCCESS;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getExperience()
	{
		return experience;
	}

	public void setExperience(int experience)
	{
		this.experience = experience;
	}

	public List<User> getUsers()
	{
		return users;
	}
}
