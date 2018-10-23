package com.jimi.smt.eps.display.util;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpHelper {

	private static final String URL = "http://10.10.11.110:8080/eps_server/";

	private final OkHttpClient client = new OkHttpClient();

	
	/**@author HCJ
	 * http请求，参数存在时要求MAP，键为参数名，值为参数值
	 * @method requestHttp
	 * @param action
	 * @param args
	 * @return
	 * @throws IOException
	 * @return String
	 * @date 2018年9月25日 下午5:50:01
	 */
	public String requestHttp(String action, Map<String, String> args) throws IOException {
		String result;
		if (args != null) {
			Builder requestBuilder = new FormBody.Builder();
			for (Map.Entry<String, String> entry : args.entrySet()) {
				requestBuilder.add(entry.getKey(), entry.getValue());
			}
			RequestBody requestBody = requestBuilder.build();
			Request request = new Request.Builder().url(URL + action).post(requestBody).build();
			Call call = client.newCall(request);
			result = call.execute().body().string();
		} else {
			Request request = new Request.Builder().url(URL + action).build();
			Call call = client.newCall(request);
			result = call.execute().body().string();
		}
		return result;
	}

	
	/**@author HCJ
	 * http请求，参数存在时要求MAP，键为参数名，值为参数值
	 * @method requestHttp
	 * @param action
	 * @param args
	 * @param callback
	 * @throws IOException
	 * @return void
	 * @date 2018年9月25日 下午5:50:31
	 */
	public void requestHttp(String action, Map<String, String> args, Callback callback) throws IOException {
		Builder requestBuilder = new FormBody.Builder();
		if (args != null) {
			for (Map.Entry<String, String> entry : args.entrySet()) {
				requestBuilder.add(entry.getKey(), entry.getValue());
			}
		}
		RequestBody requestBody = requestBuilder.build();
		Request request = new Request.Builder().url(URL + action).post(requestBody).build();
		Call call = client.newCall(request);
		call.enqueue(callback);
	}

}
