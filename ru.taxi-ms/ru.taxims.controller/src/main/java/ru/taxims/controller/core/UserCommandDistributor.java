package ru.taxims.controller.core;

import com.google.gson.JsonObject;
import ru.taxims.controller.wrapper.Wrapper;

/**
 * Created by Developer_DB on 20.11.14.
 */
public interface UserCommandDistributor
{
	public Wrapper getWrapper(long userId, JsonObject jsonObject);
}
