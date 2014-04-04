package android.tgudapp.service;

public class ServiceResponse {
	
	private ServiceAction _action;
	private ResultCode _code;
	private Object _data;
	public ServiceResponse(ServiceAction _action, Object _data, ResultCode _code) {
		this._action = _action;
		this._data = _data;
		this._code = _code;
	}
	public ServiceResponse(ServiceAction _action, Object _data) {
		this(_action, _data, ResultCode.Success);
	}
	
	public boolean isSuccess(){
		return(_code == ResultCode.Success);
	}
	public ServiceAction get_action() {
		return _action;
	}
	
	public ResultCode get_code() {
		return _code;
	}
	
	public Object get_data() {
		return _data;
	}
	
	
	
}
