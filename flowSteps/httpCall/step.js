/**
 * This flow step will send generic request.
 *
 * @param {object} inputs
 * {text} method, This is used to config method.
 * {text} url, This is used to config external URL.
 * {Array[string]} pathVariables, This is used to config path variables.
 * {Array[string]} headers, This is used to config headers.
 * {Array[string]} params, This is used to config params.
 * {string} body, This is used to send body request.
 * {string} callbackData, This is used to send callback data.
 * {text} callbacks, This is used to send callbacks.
 * {boolean} followRedirects, This is used to config follow redirects.
 * {boolean} download, This is used to config download.
 * {boolean} fullResponse, This is used to config full response.
 * {number} connectionTimeout, Read timeout interval, in milliseconds.
 * {number} readTimeout, Connect timeout interval, in milliseconds.
 */
step.httpCall = function (inputs) {

	var inputs = {
		headers: inputs.headers || [],
		params: inputs.params || [],
		body: inputs.body || {},
		download: inputs.download || false,
		fileName: inputs.fileName || "",
		fullResponse: inputs.fullResponse || false,
		connectionTimeout: inputs.connectionTimeout || 5000,
		readTimeout: inputs.readTimeout || 60000,
		events: inputs.events || "",
		url: inputs.url || {
			urlValue: "",
			paramsValue: []
		},
		path: inputs.path || ""
	};

	var headers = isObject(inputs.headers) ? inputs.headers : stringToObject(inputs.headers)
	var params = isObject(inputs.params) ? inputs.params : stringToObject(inputs.params)
	var body = isObject(inputs.body) ? inputs.body : JSON.parse(inputs.body);

	var options = {
		path: parse(inputs.url.urlValue, inputs.url.paramsValue),
		params:params,
		headers:headers,
		body: body,
		followRedirects : inputs.followRedirects,
		forceDownload :inputs.download,
		downloadSync : false,
		fileName: inputs.fileName,
		fullResponse : inputs.fullResponse,
		connectionTimeout: inputs.connectionTimeout,
		readTimeout: inputs.readTimeout,
		defaultCallback: !!inputs.events
	}

	switch (inputs.method) {
		case 'get':
			return endpoint._get(options);
		case 'post':
			return endpoint._post(options);
		case 'delete':
			return endpoint._delete(options);
		case 'put':
			return endpoint._put(options);
		case 'connect':
			return endpoint._connect(options);
		case 'head':
			return endpoint._head(options);
		case 'options':
			return endpoint._options(options);
		case 'patch':
			return endpoint._patch(options);
		case 'trace':
			return endpoint._trace(options);
		default:
			return null;
	}

};

var parse = function (url, pathVariables){

	var regex = /{([^}]*)}/g;

	if (!url.match(regex)){
		return url;
	}

	if(!pathVariables){
		sys.logs.error('No path variables have been received and the url contains curly brackets\'{}\'');
		throw new Error('Error please contact support.');
	}

	url = url.replace(regex, function(m, i) {
		return pathVariables[i] ? pathVariables[i] : m;
	})

	return url;
}
var isObject = function (obj) {
	return !!obj && stringType(obj) === '[object Object]'
};

var stringType = Function.prototype.call.bind(Object.prototype.toString);

var stringToObject = function (obj) {
	if (!!obj){
		var keyValue = obj.toString().split(',');
		var parseObj = {};
		for(var i = 0; i < keyValue.length; i++) {
			parseObj[keyValue[i].split('=')[0]] = keyValue[i].split('=')[1]
		}
		return parseObj;
	}
	return null;
};