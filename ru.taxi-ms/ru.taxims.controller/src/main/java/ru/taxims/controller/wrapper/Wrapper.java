package ru.taxims.controller.wrapper;

/**
 * Created by Developer_DB on 18.11.14.
 */
public class Wrapper
{
	public String command;
	public int version = 1;
	public Object content;
	public boolean response;

	public Wrapper(){}

	public Wrapper(String command)
	{
		this.command = command;
	}

	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public Object getContent()
	{
		return content;
	}

	public void setContent(Object content)
	{
		this.content = content;
	}

	public boolean isResponse()
	{
		return response;
	}

	public void setResponse(boolean response)
	{
		this.response = response;
	}
}
