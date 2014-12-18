package application;

import service.CarModelRestServiceBean;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Developer_DB on 20.10.14.
 */
@ApplicationPath("rs")
public class ApplicationConfigBean extends Application
{
	private final Set<Class<?>> classes;
	public ApplicationConfigBean() {
		HashSet<Class<?>> c = new HashSet<Class<?>>();
		c.add(CarModelRestServiceBean.class);
		classes = Collections.unmodifiableSet(c);
	}
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}
