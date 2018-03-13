package com.xcgmym.jiajuwulian;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import org.json.JSONException;

public class YongHu
{
	private Socket socket = null;
	private Timer timer = null;
	private YongHuJianTing yongHuJianTing = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;

	private String id;
	private boolean isDengLu = false;

	public YongHu(Socket arg, YongHuJianTing yhjt)
	{
		socket = arg;
		yongHuJianTing = yhjt;

		System.out.println("得到一个用户连接");

		try
		{
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

		}catch(IOException ioe)
		{
			System.out.println("获取输入输出流失败");
			close();
			return;
		}

		timer = new Timer("YongHu", true);
		timer.schedule(new TimerTask()
				{
					int count = 0;
					int tmp = 0;

					public void run()
					{
						try
						{
							count = inputStream.available();
							if(count>0)
							{
								byte[] res = new byte[count];
								for(int i=0; i<count; i++)
								{
									tmp = inputStream.read();
									res[i] = (byte)tmp;
								}
								chuLi(res);
								count = inputStream.available();
								if(count == 0)
								{
									tmp = inputStream.read();
									if(tmp == -1)
									{
										System.out.println("设备断线，退出当前");
										close();
									}
								}
							}
						}catch(IOException ioe)
						{
							System.out.println("读取失败，退出用户");
							close();
						}
					}
				}, 100, 100);
	}

	private void chuLi(byte[] arg)
	{
		JSONObject json = new JSONObject(new String(arg));
		String qingQiu = "";

		try
		{
			qingQiu = json.getString("QingQiu");
		}catch(JSONException je)
		{
			System.out.println("获取Json的请求值错误");
			return;
	       	}

		if(qingQiu.equals("DengLu"))
		{
			try
			{
				id = json.getString("WoShi");
			}catch(JSONException je)
			{
				System.out.println("获取Id的请求值错误");
				return;
			}

			if(id == null)
			{
				JSONObject jsonRes = new JSONObject();
				jsonRes.put("HuiFu", "IDWeiKong");
				return;
			}
			if(yongHuJianTing != null)
			{
				yongHuJianTing.yongHuShangXian(id, this);
				isDengLu = true;
				return;
			}
		}

		if(isDengLu == false)
		{
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("HuiFu", "QingDengLu");
			faSong(jsonRes.toString());
			return;
		}
		if(qingQiu.equals("DengChu"))
		{
			if(yongHuJianTing != null)
			{
				yongHuJianTing.yongHuXiaXian(id);
				isDengLu = false;
				return;
			}
		}

		if(qingQiu.equals("FaSong"))
		{
			String faSongDao = "";
			String laiZi = "";
			Object obj = null;
			try
			{
				faSongDao = json.getString("FaSongDao");
				laiZi = json.getString("WoShi");
			}catch(JSONException jse)
			{
			}
			if(yongHuJianTing != null)
			{
				yongHuJianTing.yongHuMingLing(faSongDao, laiZi, json);
				return;
			}
		}
	}

	public void faSong(String arg)
	{
		try
		{
			outputStream.write(arg.getBytes());
			outputStream.flush();
		}catch(IOException ioe)
		{
			System.out.println("设备"+id+"异常掉线，退出");
			close();
		}
	}

	public void close()
	{
		System.out.println("用户退出");
		if(timer != null)
		{
			timer.cancel();
		}

		if(inputStream != null)
		{
			try
			{
				inputStream.close();
			}catch(IOException ioe)
			{
				System.out.println("失败");
			}
		}

		if(outputStream != null)
		{
			try
			{
				outputStream.close();
			}catch(IOException ioe)
			{
				System.out.println("失败");
			}
		}

		if(yongHuJianTing!=null)
		{
			yongHuJianTing.yongHuXiaXian(id);
		}
	}
}
