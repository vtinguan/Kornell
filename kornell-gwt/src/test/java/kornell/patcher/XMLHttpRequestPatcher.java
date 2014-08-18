package kornell.patcher;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;
 
/**
 * Patch the GWT {@link XMLHttpRequest} class.
 * Example that caused error:
 * <p>
 * <pre>
 * final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, requestURL);
 * requestBuilder.setCallback(new RequestCallback() {
 *
 *     public void onResponseReceived(Request request, Response response) {
 *         //Handle Receive.
 *     }
 *
 *     public void onError(Request request, Throwable exception) {
 *         //Handle Error.
 *     }
 * });
 *
 * try {
 *      requestBuilder.send();
 * } catch (RequestException re) {
 *
 * }
 * </pre>
 * </p>
 * @author Matthew Edwards
 */
@PatchClass(XMLHttpRequest.class)
final class XMLHttpRequestPatcher {
 
    /** Private Constructor to prevent Utility class Instantiation. */
    private XMLHttpRequestPatcher() {
    }
 
    /**
     * Patch {@link XMLHttpRequest#create() }.
     * @return a patched XMLHttpRequest instance.
     */
    @PatchMethod
    public static XMLHttpRequest create() {
        return GwtReflectionUtils.instantiateClass(XMLHttpRequest.class);
    }
 
    /**
     * Patch {@link XMLHttpRequest#open(java.lang.String, java.lang.String)}.
     */
    @PatchMethod
    public static void open(final XMLHttpRequest request, final String httpMethod, final String url) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#setRequestHeader(java.lang.String, java.lang.String)}.
     */
    @PatchMethod
    public static void setRequestHeader(final XMLHttpRequest request, final String header, final String value) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#send(java.lang.String) }.
     */
    @PatchMethod
    public static void send(final XMLHttpRequest request, final String requestData) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#abort() }.
     */
    @PatchMethod
    public static void abort(final XMLHttpRequest request) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#clearOnReadyStateChange() }.
     */
    @PatchMethod
    public static void clearOnReadyStateChange(final XMLHttpRequest request) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#getAllResponseHeaders() }.
     */
    @PatchMethod
    public static String getAllResponseHeaders(final XMLHttpRequest request) {
        return "gwt-test-utils: all response headers";
    }
 
    /**
     * Patch {@link XMLHttpRequest#getReadyState() }.
     */
    @PatchMethod
    public static int getReadyState(final XMLHttpRequest request) {
        return Integer.MAX_VALUE;
    }
 
    /**
     * Patch {@link XMLHttpRequest#getResponseHeader(java.lang.String) }.
     */
    @PatchMethod
    public static String getResponseHeader(final XMLHttpRequest request, final String header) {
        return "gwt-test-utils: response header";
    }
 
    /**
     * Patch {@link XMLHttpRequest#getResponseText() }.
     */
    @PatchMethod
    public static String getResponseText(final XMLHttpRequest request) {
        return "gwt-test-utils: response Text";
    }
 
    /**
     * Patch {@link XMLHttpRequest#getStatus() }.
     */
    @PatchMethod
    public static int getStatus(final XMLHttpRequest request) {
        return Integer.MAX_VALUE;
    }
 
    /**
     * Patch {@link XMLHttpRequest#getStatusText() }.
     */
    @PatchMethod
    public static String getStatusText(final XMLHttpRequest request) {
        return "gwt-test-utils: status text";
    }
 
    /**
     * Patch {@link XMLHttpRequest#open(java.lang.String, java.lang.String, java.lang.String) }.
     */
    @PatchMethod
    public static void open(
            final XMLHttpRequest request, final String httpMethod, final String url, final String user) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#open(java.lang.String, java.lang.String, java.lang.String, java.lang.String) }.
     */
    @PatchMethod
    public static void open(final XMLHttpRequest request, final String httpMethod, final String url, final String user,
            final String password) {
    }
 
    /**
     * Patch {@link XMLHttpRequest#setOnReadyStateChange(com.google.gwt.xhr.client.ReadyStateChangeHandler) }.
     */
    @PatchMethod
    public static void setOnReadyStateChange(final XMLHttpRequest request, final ReadyStateChangeHandler handler) {
    }
}