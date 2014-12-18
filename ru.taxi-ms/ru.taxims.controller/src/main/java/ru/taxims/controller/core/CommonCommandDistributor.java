package ru.taxims.controller.core;

import com.google.gson.JsonObject;
import ru.taxims.controller.wrapper.Wrapper;

/**
 * Created by Developer_DB on 25.11.14.
 */
public interface CommonCommandDistributor
{
	public Wrapper getWrapper(JsonObject jsonObject);
}
