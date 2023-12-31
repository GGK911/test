package certTest.saxon.utils;

/***
 * 系统返回参数
 * @author saxon
 *
 */
public class ResultUtils {

	public final static int CODE_SUCCESS = 200; // 正常
	public final static int CODE_ERROR = 201; // 错误
	public final static int CODE_ERROR_NULL = 202; // 参数为空
	public final static int CODE_ERROR_PARAM = 203;// 参数错误
	public final static int CODE_EXCEPTION = 500; // 发生异常
	
	private int code;//消息码
	private String msg;//消息内容
	private Object object;//消息结果集

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ResultUtils() {
	}

	public ResultUtils(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ResultUtils(int code, Object object) {
		this.code = code;
		this.object = object;
	}
	
	public ResultUtils(int code, String msg, Object object) {
		this.code = code;
		this.msg = msg;
		this.object = object;
	}

	
}
