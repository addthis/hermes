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


import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Objects.toStringHelper;

/**
 * POJO for storing measurements as defined by the Resource Timing API
 * http://www.w3.org/TR/resource-timing/
 */
@SuppressWarnings("unused")
public class ResourceTiming {

    /**
     * The name attribute must return the identifier
     * for this PerformanceEntry object.
     * This identifier does not have to be unique.
     */
    private final String name;

    /**
     * The entryType attribute must return a DOMString that describes the type of the interface
     * represented by this PerformanceEntry object. http://www.w3.org/wiki/Web_Performance/EntryType
     * lists all of the known entryType values.
     */
    private final String entryType;

    /**
     * If the initiator is an element, on getting, the initiatorType attribute must
     * return a DOMString with the same value as the localName of that element.
     * If the initiator is a CSS resource downloaded by the url() syntax,
     * such as @import url() or background: url(), on getting, the
     * initiatorType attribute must return the DOMString "css".
     * If the initiator is an XMLHttpRequest object, on getting,
     * the initiatorType attribute must return the DOMString "xmlhttprequest".
     */
    private final String initiatorType;

    /**
     * The startTime attribute must return a DOMHighResTimeStamp
     * that contains the time value of the first recorded timestamp of this performance metric.
     */
    private final double startTime;

    /**
     * The duration attribute must return a DOMHighResTimeStamp that contains the
     * time value of the duration of the entire event being recorded by this
     * PerformanceEntry. Typically, this would be the time difference between
     * the last recorded timestamp and the first recorded timestamp of this
     * PerformanceEntry. A performance metric may choose to return a duration
     * of 0, if the duration concept doesn't apply.
     */
    private final double duration;

    /**
     * If there are HTTP redirects or equivalent when fetching the resource and if all
     * the redirects or equivalent are from the same origin as the current document,
     * this attribute must return the starting time of the fetch that initiates the redirect.
     * <p/>
     * If there are HTTP redirects or equivalent when fetching the resource and if any of
     * the redirects are not from the same origin as the current document, but
     * the timing allow check algorithm passes for each redirected resource,
     * this attribute must return the starting time of the fetch that initiates
     * the redirect. Otherwise, this attribute must return zero.
     */
    private final double redirectStart;

    /**
     * If there are HTTP redirects or equivalent when fetching the resource and if
     * all the redirects or equivalent are from the same origin as the current document,
     * this attribute must return the time immediately after receiving
     * the last byte of the response of the last redirect.
     * <p/>
     * If there are HTTP redirects or equivalent when fetching the resource
     * and if any of the redirects are not from the same origin as
     * the current document, but the timing allow check algorithm passes
     * for each redirected resource, this attribute must return
     * the time immediately after receiving the last byte of the
     * response of the last redirect. Otherwise, this attribute must return zero.
     */
    private final double redirectEnd;

    /**
     * If there are no HTTP redirects or equivalent, this attribute must
     * return the time immediately before the user agent starts to fetch the resource.
     * If there are HTTP redirects or equivalent, this attribute must return
     * the time immediately before the user agent starts to fetch the final
     * resource in the redirection.
     */
    private final double fetchStart;

    /**
     * This attribute must return the time immediately before the user agent
     * starts the domain name lookup for the resource. If a persistent connection
     * [RFC 2616] is used or the resource is retrieved from relevant application caches
     * or local resources, this attribute must return the same value as fetchStart.
     * If the last non-redirected fetch of the resource is not the same origin as
     * the current document, domainLookupStart must return zero unless the
     * timing allow check algorithm passes.
     */
    private final double domainLookupStart;

    /**
     * This attribute must return the time immediately after the user agent finishes the
     * domain name lookup for the resource. If a persistent connection [RFC 2616] is used
     * or the resource is retrieved from relevant application caches or local resources,
     * this attribute must return the same value as fetchStart.
     * <p/>
     * If the user agent has the domain information in cache, domainLookupStart and domainLookupEnd
     * represent the times when the user agent starts and ends the domain data retrieval from the cache.
     * If the last non-redirected fetch of the resource is not the same origin as the
     * current document, domainLookupEnd must return zero unless the timing allow check algorithm passes.
     */
    private final double domainLookupEnd;

    /**
     * This attribute must return the time immediately before the user agent start establishing
     * the connection to the server to retrieve the resource. If a persistent connection
     * [RFC 2616] is used or the resource is retrieved from relevant application caches
     * or local resources, this attribute must return value of domainLookupEnd.
     * <p/>
     * If the last non-redirected fetch of the resource is not the same origin
     * as the current document, connectStart must return zero unless
     * timing allow check algorithm passes.
     */
    private final double connectStart;

    /**
     * This attribute must return the time immediately after the user agent finishes establishing
     * the connection to the server to retrieve the resource. If a persistent connection
     * [RFC 2616] is used or the resource is retrieved from relevant application caches
     * or local resources, this attribute must return the value of domainLookupEnd.
     * <p/>
     * If the transport connection fails and the user agent reopens a connection,
     * connectStart and connectEnd should return the corresponding values of the new connection.
     * connectEnd must include the time interval to establish the transport
     * connection, as well as other time intervals such as SSL handshake and SOCKS authentication.
     * If the last non-redirected fetch of the resource is not the same origin
     * as the current document, connectEnd must return zero unless the timing allow check algorithm passes.
     */
    private final double connectEnd;

    /**
     * This attribute is optional. User agents that don't have this attribute available must
     * set it as undefined. When this attribute is available, if the scheme of the current
     * page is HTTPS, this attribute must return the time immediately before the user agent
     * starts the handshake process to secure the current connection. If the secureConnectionStart
     * attribute is available but HTTPS is not used, this attribute must return zero.
     * <p/>
     * If the last non-redirected fetch of the resource is not the same origin as the
     * current document, secureConnectionStart must return zero unless the timing allow
     * check algorithm passes.
     */
    private final double secureConnectionStart;

    /**
     * This attribute must return the time immediately before the user agent starts requesting
     * the resource from the server, or from relevant application caches or from local resources.
     * If the transport connection fails after a request is sent and the user agent reopens
     * a connection and resend the request, requestStart must return the corresponding
     * values of the new request. If the last non-redirected fetch of the resource is not
     * the same origin as the current document, requestStart must return zero unless the
     * timing allow check algorithm passes.
     */
    private final double requestStart;

    /**
     * This attribute must return the time immediately after the user agent receives the first byte
     * of the response from the server, or from relevant application caches or from local resources.
     * If the last non-redirected fetch of the resource is not the same origin as the current document,
     * responseStart must return zero unless the timing allow check algorithm passes.
     */
    private final double responseStart;

    /**
     * This attribute must return the time immediately after the user agent finishes receiving the last
     * byte of the resource from relevant application caches or from local resources.
     */
    private final double responseEnd;

    @JsonCreator
    public ResourceTiming(@JsonProperty("name") String name,
                       @JsonProperty("entryType") String entryType,
                       @JsonProperty("initiatorType") String initiatorType,
                       @JsonProperty("startTime") double startTime,
                       @JsonProperty("duration") double duration,
                       @JsonProperty("redirectStart") double redirectStart,
                       @JsonProperty("redirectEnd") double redirectEnd,
                       @JsonProperty("fetchStart") double fetchStart,
                       @JsonProperty("domainLookupStart") double domainLookupStart,
                       @JsonProperty("domainLookupEnd") double domainLookupEnd,
                       @JsonProperty("connectStart") double connectStart,
                       @JsonProperty("connectEnd") double connectEnd,
                       @JsonProperty("secureConnectionStart") double secureConnectionStart,
                       @JsonProperty("requestStart") double requestStart,
                       @JsonProperty("responseStart") double responseStart,
                       @JsonProperty("responseEnd") double responseEnd) {
        this.name = name;
        this.entryType = entryType;
        this.initiatorType = initiatorType;
        this.startTime = startTime;
        this.duration = duration;
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
    }

    public String getName() {
        return name;
    }

    public String getEntryType() {
        return entryType;
    }

    public String getInitiatorType() {
        return initiatorType;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getDuration() {
        return duration;
    }

    public double getRedirectStart() {
        return redirectStart;
    }

    public double getRedirectEnd() {
        return redirectEnd;
    }

    public double getFetchStart() {
        return fetchStart;
    }

    public double getDomainLookupStart() {
        return domainLookupStart;
    }

    public double getDomainLookupEnd() {
        return domainLookupEnd;
    }

    public double getConnectStart() {
        return connectStart;
    }

    public double getConnectEnd() {
        return connectEnd;
    }

    public double getSecureConnectionStart() {
        return secureConnectionStart;
    }

    public double getRequestStart() {
        return requestStart;
    }

    public double getResponseStart() {
        return responseStart;
    }

    public double getResponseEnd() {
        return responseEnd;
    }

    @Override public String toString() {
        return toStringHelper(this)
                .add("name", name)
                .add("entryType", entryType)
                .add("initiatorType", initiatorType)
                .add("startTime", startTime)
                .add("duration", duration)
                .add("redirectStart", redirectStart)
                .add("redirectEnd", redirectEnd)
                .add("fetchStart", fetchStart)
                .add("domainLookupStart", domainLookupStart)
                .add("domainLookupEnd", domainLookupEnd)
                .add("connectStart", connectStart)
                .add("connectEnd", connectEnd)
                .add("secureConnectionStart", secureConnectionStart)
                .add("requestStart", requestStart)
                .add("responseStart", responseStart)
                .add("responseEnd", responseEnd)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,
                            entryType,
                            initiatorType,
                            startTime,
                            duration,
                            redirectStart,
                            redirectEnd,
                            fetchStart,
                            domainLookupStart,
                            domainLookupEnd,
                            connectStart,
                            connectEnd,
                            secureConnectionStart,
                            requestStart,
                            responseStart,
                            redirectEnd);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ResourceTiming)) return false;

        ResourceTiming that = (ResourceTiming) other;

        if (connectEnd != that.connectEnd) return false;
        if (connectStart != that.connectStart) return false;
        if (domainLookupEnd != that.domainLookupEnd) return false;
        if (domainLookupStart != that.domainLookupStart) return false;
        if (duration != that.duration) return false;
        if (fetchStart != that.fetchStart) return false;
        if (redirectEnd != that.redirectEnd) return false;
        if (redirectStart != that.redirectStart) return false;
        if (requestStart != that.requestStart) return false;
        if (responseEnd != that.responseEnd) return false;
        if (responseStart != that.responseStart) return false;
        if (secureConnectionStart != that.secureConnectionStart) return false;
        if (startTime != that.startTime) return false;
        if (entryType != null ? !entryType.equals(that.entryType) : that.entryType != null) return false;
        if (initiatorType != null ? !initiatorType.equals(that.initiatorType) : that.initiatorType != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private String name;
        private String entryType;
        private String initiatorType;
        private double startTime;
        private double duration;
        private double redirectStart;
        private double redirectEnd;
        private double fetchStart;
        private double domainLookupStart;
        private double domainLookupEnd;
        private double connectStart;
        private double connectEnd;
        private double secureConnectionStart;
        private double requestStart;
        private double responseStart;
        private double responseEnd;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEntryType(String entryType) {
            this.entryType = entryType;
            return this;
        }

        public Builder setInitiatorType(String initiatorType) {
            this.initiatorType = initiatorType;
            return this;
        }

        public Builder setStartTime(double startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }

        public Builder setRedirectStart(double redirectStart) {
            this.redirectStart = redirectStart;
            return this;
        }

        public Builder setRedirectEnd(double redirectEnd) {
            this.redirectEnd = redirectEnd;
            return this;
        }

        public Builder setFetchStart(double fetchStart) {
            this.fetchStart = fetchStart;
            return this;
        }

        public Builder setDomainLookupStart(double domainLookupStart) {
            this.domainLookupStart = domainLookupStart;
            return this;
        }

        public Builder setDomainLookupEnd(double domainLookupEnd) {
            this.domainLookupEnd = domainLookupEnd;
            return this;
        }

        public Builder setConnectStart(double connectStart) {
            this.connectStart = connectStart;
            return this;
        }

        public Builder setConnectEnd(double connectEnd) {
            this.connectEnd = connectEnd;
            return this;
        }

        public Builder setSecureConnectionStart(double secureConnectionStart) {
            this.secureConnectionStart = secureConnectionStart;
            return this;
        }

        public Builder setRequestStart(double requestStart) {
            this.requestStart = requestStart;
            return this;
        }

        public Builder setResponseStart(double responseStart) {
            this.responseStart = responseStart;
            return this;
        }

        public Builder setResponseEnd(double responseEnd) {
            this.responseEnd = responseEnd;
            return this;
        }

        public Builder(ResourceTiming original) {
            this.name = original.name;
            this.entryType = original.entryType;
            this.initiatorType = original.initiatorType;
            this.startTime = original.startTime;
            this.duration = original.duration;
            this.redirectStart = original.redirectStart;
            this.redirectEnd = original.redirectEnd;
            this.fetchStart = original.fetchStart;
            this.domainLookupStart = original.domainLookupStart;
            this.domainLookupEnd = original.domainLookupEnd;
            this.connectStart = original.connectStart;
            this.connectEnd = original.connectEnd;
            this.secureConnectionStart = original.secureConnectionStart;
            this.requestStart = original.requestStart;
            this.responseStart = original.responseStart;
            this.responseEnd = original.responseEnd;
        }

        public ResourceTiming build() {
            return new ResourceTiming(name, entryType, initiatorType,
                                   startTime, duration, redirectStart, redirectEnd,
                                   fetchStart, domainLookupStart, domainLookupEnd,
                                   connectStart, connectEnd, secureConnectionStart,
                                   requestStart, responseStart, responseEnd);
        }

    }

}
