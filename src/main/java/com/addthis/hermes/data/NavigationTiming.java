/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addthis.hermes.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for storing measurements as defined by the Navigation Timing API
 * http://www.w3.org/TR/navigation-timing/
 */
@SuppressWarnings("unused")
public class NavigationTiming {

    /**
     * This attribute must return the time immediately after the user
     * agent finishes prompting to unload the previous document.
     * If there is no previous document, this attribute must return
     * the time the current document is created.
     */
    private final long navigationStart;


    /**
     * If the previous document and the current document have the same
     * origin [IETF RFC 6454], this attribute must return the time
     * immediately before the user agent starts the unload event
     * of the previous document. If there is no previous document
     * or the previous document has a different origin than the current
     * document, this attribute must return zero.
     */
    private final long unloadEventStart;

    /**
     * If the previous document and the current document have the same
     * same origin, this attribute must return the time immediately
     * after the user agent finishes the unload event of the previous
     * document. If there is no previous document or the previous
     * document has a different origin than the current document
     * or the unload is not yet completed, this attribute must return zero.
     *
     * If there are HTTP redirects or equivalent when navigating and not
     * all the redirects or equivalent are from the same origin,
     * both unloadEventStart and unloadEventEnd must return the zero.
     */
    private final long unloadEventEnd;

    /**
     * If there are HTTP redirects or equivalent when navigating
     * and if all the redirects or equivalent are from the same origin
     * , this attribute must return the starting time of the fetch that
     * initiates the redirect. Otherwise, this attribute must return zero.
     */
    private final long redirectStart;

    /**
     * If there are HTTP redirects or equivalent when navigating and all
     * redirects and equivalents are from the same origin, this attribute
     * must return the time immediately after receiving the last byte of
     * the response of the last redirect. Otherwise, this attribute must return zero.
     */
    private final long redirectEnd;

    /**
     * If the new resource is to be fetched using HTTP GET or equivalent,
     * fetchStart must return the time immediately before the user agent
     * starts checking any relevant application caches. Otherwise, it
     * must return the time when the user agent starts fetching the resource.
     */
    private final long fetchStart;

    /**
     * This attribute must return the time immediately before the user agent
     * starts the domain name lookup for the current document. If a persistent
     * connection [RFC 2616] is used or the current document is retrieved
     * from relevant application caches or local resources, this attribute
     * must return the same value as fetchStart.
     */
    private final long domainLookupStart;

    /**
     * This attribute must return the time immediately after the user agent
     * finishes the domain name lookup for the current document. If a persistent
     * connection [RFC 2616] is used or the current document is retrieved from
     * relevant application caches or local resources, this attribute must
     * return the same value as fetchStart.
     */
    private final long domainLookupEnd;

    /**
     * This attribute must return the time immediately before the user agent
     * start establishing the connection to the server to retrieve the document.
     * If a persistent connection [RFC 2616] is used or the current document is
     * retrieved from relevant application caches or local resources, this
     * attribute must return value of domainLookupEnd.
     */
    private final long connectStart;

    /**
     * This attribute must return the time immediately after the user agent finishes
     * establishing the connection to the server to retrieve the current document.
     * If a persistent connection [RFC 2616] is used or the current document is
     * retrieved from relevant application caches or local resources, this
     * attribute must return the value of domainLookupEnd
     *
     * If the transport connection fails and the user agent reopens a
     * connection, connectStart and connectEnd should return the
     * corresponding values of the new connection.
     *
     * connectEnd must include the time interval to establish the
     * transport connection as well as other time interval such as
     * SSL handshake and SOCKS authentication.
     */
    private final long connectEnd;

    /**
     * This attribute is optional. User agents that don't have this attribute
     * available must set it as undefined. When this attribute is available,
     * if the scheme of the current page is HTTPS, this attribute must return
     * the time immediately before the user agent starts the handshake process
     * to secure the current connection. If this attribute is available but
     * HTTPS is not used, this attribute must return zero.
     */
    private final long secureConnectionStart;

    /**
     * This attribute must return the time immediately before the user agent starts
     * requesting the current document from the server, or from relevant application
     * caches or from local resources.
     *
     * If the transport connection fails after a request is sent and the user
     * agent reopens a connection and resend the request,
     * requestStart should return the corresponding values of the new request.
     *
     * This interface does not include an attribute to represent the completion
     * of sending the request, e.g., requestEnd.
     *
     * Completion of sending the request from the user agent does not always
     * indicate the corresponding completion time in the network transport,
     * which brings most of the benefit of having such an attribute.
     * Some user agents have high cost to determine the actual completion
     * time of sending the request due to the HTTP layer encapsulation.
     */
    private final long requestStart;

    /**
     * This attribute must return the time immediately after the user agent
     * receives the first byte of the response from the server, or from
     * relevant application caches or from local resources.
     */
    private final long responseStart;

    /**
     * This attribute must return the time immediately after the user agent
     * receives the last byte of the current document or immediately
     * before the transport connection is closed, whichever comes first.
     * The document here can be received either from the server, relevant
     * application caches or from local resources.
     */
    private final long responseEnd;

    /**
     * This attribute must return the time immediately before the user agent
     * sets the current document readiness to "loading".
     */
    private final long domLoading;

    /**
     * This attribute must return the time immediately before the user agent
     * sets the current document readiness to "interactive".
     */
    private final long domInteractive;

    /**
     * This attribute must return the time immediately before the user agent
     * fires the DOMContentLoaded event at the Document.
     */
    private final long domContentLoadedEventStart;

    /**
     * This attribute must return the time immediately after the document's
     * DOMContentLoaded event completes.
     */
    private final long domContentLoadedEventEnd;

    /**
     * This attribute must return the time immediately before the user agent
     * sets the current document readiness to "complete".
     *
     * If the current document readiness changes to the same state
     * multiple times, domLoading, domInteractive, domContentLoadedEventStart,
     * domContentLoadedEventEnd and domComplete must return the time of
     * the first occurrence of the corresponding document readiness change.
     */
    private final long domComplete;

    /**
     * This attribute must return the time immediately before the
     * load event of the current document is fired. It must return
     * zero when the load event is not fired yet.
     */
    private final long loadEventStart;

    /**
     * This attribute must return the time when the load event of
     * the current document is completed. It must return zero when
     * the load event is not fired or is not completed.
     */
    private final long loadEventEnd;

    @JsonCreator
    public NavigationTiming(
            @JsonProperty("navigationStart") long navigationStart,
            @JsonProperty("unloadEventStart") long unloadEventStart,
            @JsonProperty("unloadEventEnd") long unloadEventEnd,
            @JsonProperty("redirectStart") long redirectStart,
            @JsonProperty("redirectEnd") long redirectEnd,
            @JsonProperty("fetchStart") long fetchStart,
            @JsonProperty("domainLookupStart") long domainLookupStart,
            @JsonProperty("domainLookupEnd") long domainLookupEnd,
            @JsonProperty("connectStart") long connectStart,
            @JsonProperty("connectEnd") long connectEnd,
            @JsonProperty("secureConnectionStart") long secureConnectionStart,
            @JsonProperty("requestStart") long requestStart,
            @JsonProperty("responseStart") long responseStart,
            @JsonProperty("responseEnd") long responseEnd,
            @JsonProperty("domLoading") long domLoading,
            @JsonProperty("domInteractive") long domInteractive,
            @JsonProperty("domContentLoadedEventStart") long domContentLoadedEventStart,
            @JsonProperty("domContentLoadedEventEnd") long domContentLoadedEventEnd,
            @JsonProperty("domComplete") long domComplete,
            @JsonProperty("loadEventStart") long loadEventStart,
            @JsonProperty("loadEventEnd") long loadEventEnd) {
        this.navigationStart = navigationStart;
        this.unloadEventStart = unloadEventStart;
        this.unloadEventEnd = unloadEventEnd;
        this.redirectStart = redirectStart;
        this.redirectEnd = redirectEnd;
        this.fetchStart = fetchStart;
        this.domainLookupStart = domainLookupStart;
        this.domainLookupEnd = domainLookupEnd;
        this.connectStart = connectStart;
        this.connectEnd = connectEnd;
        this.secureConnectionStart = secureConnectionStart;
        this.requestStart = requestStart;
        this.responseStart = responseStart;
        this.responseEnd = responseEnd;
        this.domLoading = domLoading;
        this.domInteractive = domInteractive;
        this.domContentLoadedEventStart = domContentLoadedEventStart;
        this.domContentLoadedEventEnd = domContentLoadedEventEnd;
        this.domComplete = domComplete;
        this.loadEventStart = loadEventStart;
        this.loadEventEnd = loadEventEnd;
    }

    public long getNavigationStart() {
        return navigationStart;
    }

    public long getUnloadEventStart() {
        return unloadEventStart;
    }

    public long getUnloadEventEnd() {
        return unloadEventEnd;
    }

    public long getRedirectStart() {
        return redirectStart;
    }

    public long getRedirectEnd() {
        return redirectEnd;
    }

    public long getFetchStart() {
        return fetchStart;
    }

    public long getDomainLookupStart() {
        return domainLookupStart;
    }

    public long getDomainLookupEnd() {
        return domainLookupEnd;
    }

    public long getConnectStart() {
        return connectStart;
    }

    public long getConnectEnd() {
        return connectEnd;
    }

    public long getSecureConnectionStart() {
        return secureConnectionStart;
    }

    public long getRequestStart() {
        return requestStart;
    }

    public long getResponseStart() {
        return responseStart;
    }

    public long getResponseEnd() {
        return responseEnd;
    }

    public long getDomLoading() {
        return domLoading;
    }

    public long getDomInteractive() {
        return domInteractive;
    }

    public long getDomContentLoadedEventStart() {
        return domContentLoadedEventStart;
    }

    public long getDomContentLoadedEventEnd() {
        return domContentLoadedEventEnd;
    }

    public long getDomComplete() {
        return domComplete;
    }

    public long getLoadEventStart() {
        return loadEventStart;
    }

    public long getLoadEventEnd() {
        return loadEventEnd;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("navigationStart:").append(navigationStart);
        sb.append(", unloadEventStart:").append(unloadEventStart);
        sb.append(", unloadEventEnd:").append(unloadEventEnd);
        sb.append(", redirectStart:").append(redirectStart);
        sb.append(", redirectEnd:").append(redirectEnd);
        sb.append(", fetchStart:").append(fetchStart);
        sb.append(", domainLookupStart:").append(domainLookupStart);
        sb.append(", domainLookupEnd:").append(domainLookupEnd);
        sb.append(", connectStart:").append(connectStart);
        sb.append(", connectEnd:").append(connectEnd);
        sb.append(", secureConnectionStart:").append(secureConnectionStart);
        sb.append(", requestStart:").append(requestStart);
        sb.append(", responseStart:").append(responseStart);
        sb.append(", responseEnd:").append(responseEnd);
        sb.append(", domLoading:").append(domLoading);
        sb.append(", domInteractive:").append(domInteractive);
        sb.append(", domContentLoadedEventStart:").append(domContentLoadedEventStart);
        sb.append(", domContentLoadedEventEnd:").append(domContentLoadedEventEnd);
        sb.append(", domComplete:").append(domComplete);
        sb.append(", loadEventStart:").append(loadEventStart);
        sb.append(", loadEventEnd:").append(loadEventEnd);
        sb.append('}');
        return sb.toString();
    }
}
