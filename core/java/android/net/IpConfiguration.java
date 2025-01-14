/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.net;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.annotation.SystemApi;
import android.compat.annotation.UnsupportedAppUsage;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * A class representing a configured network.
 * @hide
 */
@SystemApi
public final class IpConfiguration implements Parcelable {
    private static final String TAG = "IpConfiguration";

    // This enum has been used by apps through reflection for many releases.
    // Therefore they can't just be removed. Duplicating these constants to
    // give an alternate SystemApi is a worse option than exposing them.
    @SuppressLint("Enum")
    public enum IpAssignment {
        /* Use statically configured IP settings. Configuration can be accessed
         * with staticIpConfiguration */
        STATIC,
        /* Use dynamically configured IP settings */
        DHCP,
        /* no IP details are assigned, this is used to indicate
         * that any existing IP settings should be retained */
        UNASSIGNED
    }

    /** @hide */
    public IpAssignment ipAssignment;

    /** @hide */
    public StaticIpConfiguration staticIpConfiguration;

    // This enum has been used by apps through reflection for many releases.
    // Therefore they can't just be removed. Duplicating these constants to
    // give an alternate SystemApi is a worse option than exposing them.
    @SuppressLint("Enum")
    public enum ProxySettings {
        /* No proxy is to be used. Any existing proxy settings
         * should be cleared. */
        NONE,
        /* Use statically configured proxy. Configuration can be accessed
         * with httpProxy. */
        STATIC,
        /* no proxy details are assigned, this is used to indicate
         * that any existing proxy settings should be retained */
        UNASSIGNED,
        /* Use a Pac based proxy.
         */
        PAC
    }

    /** @hide */
    public ProxySettings proxySettings;

    /** @hide */
    @UnsupportedAppUsage
    public ProxyInfo httpProxy;

    private void init(IpAssignment ipAssignment,
                      ProxySettings proxySettings,
                      StaticIpConfiguration staticIpConfiguration,
                      ProxyInfo httpProxy) {
        this.ipAssignment = ipAssignment;
        this.proxySettings = proxySettings;
        this.staticIpConfiguration = (staticIpConfiguration == null) ?
                null : new StaticIpConfiguration(staticIpConfiguration);
        this.httpProxy = (httpProxy == null) ?
                null : new ProxyInfo(httpProxy);
    }

    public IpConfiguration() {
        init(IpAssignment.UNASSIGNED, ProxySettings.UNASSIGNED, null, null);
    }

    /** @hide */
    @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.R, trackingBug = 170729553)
    public IpConfiguration(IpAssignment ipAssignment,
                           ProxySettings proxySettings,
                           StaticIpConfiguration staticIpConfiguration,
                           ProxyInfo httpProxy) {
        init(ipAssignment, proxySettings, staticIpConfiguration, httpProxy);
    }

    public IpConfiguration(@NonNull IpConfiguration source) {
        this();
        if (source != null) {
            init(source.ipAssignment, source.proxySettings,
                 source.staticIpConfiguration, source.httpProxy);
        }
    }

    public @NonNull IpAssignment getIpAssignment() {
        return ipAssignment;
    }

    public void setIpAssignment(@NonNull IpAssignment ipAssignment) {
        this.ipAssignment = ipAssignment;
    }

    public @Nullable StaticIpConfiguration getStaticIpConfiguration() {
        return staticIpConfiguration;
    }

    public void setStaticIpConfiguration(@Nullable StaticIpConfiguration staticIpConfiguration) {
        this.staticIpConfiguration = staticIpConfiguration;
    }

    public @NonNull ProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(@NonNull ProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    public @Nullable ProxyInfo getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(@Nullable ProxyInfo httpProxy) {
        this.httpProxy = httpProxy;
    }

    @Override
    public String toString() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("IP assignment: " + ipAssignment.toString());
        sbuf.append("\n");
        if (staticIpConfiguration != null) {
            sbuf.append("Static configuration: " + staticIpConfiguration.toString());
            sbuf.append("\n");
        }
        sbuf.append("Proxy settings: " + proxySettings.toString());
        sbuf.append("\n");
        if (httpProxy != null) {
            sbuf.append("HTTP proxy: " + httpProxy.toString());
            sbuf.append("\n");
        }

        return sbuf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof IpConfiguration)) {
            return false;
        }

        IpConfiguration other = (IpConfiguration) o;
        return this.ipAssignment == other.ipAssignment &&
                this.proxySettings == other.proxySettings &&
                Objects.equals(this.staticIpConfiguration, other.staticIpConfiguration) &&
                Objects.equals(this.httpProxy, other.httpProxy);
    }

    @Override
    public int hashCode() {
        return 13 + (staticIpConfiguration != null ? staticIpConfiguration.hashCode() : 0) +
               17 * ipAssignment.ordinal() +
               47 * proxySettings.ordinal() +
               83 * httpProxy.hashCode();
    }

    /** Implement the Parcelable interface */
    public int describeContents() {
        return 0;
    }

    /** Implement the Parcelable interface */
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ipAssignment.name());
        dest.writeString(proxySettings.name());
        dest.writeParcelable(staticIpConfiguration, flags);
        dest.writeParcelable(httpProxy, flags);
    }

    /** Implement the Parcelable interface */
    public static final @NonNull Creator<IpConfiguration> CREATOR =
        new Creator<IpConfiguration>() {
            public IpConfiguration createFromParcel(Parcel in) {
                IpConfiguration config = new IpConfiguration();
                config.ipAssignment = IpAssignment.valueOf(in.readString());
                config.proxySettings = ProxySettings.valueOf(in.readString());
                config.staticIpConfiguration = in.readParcelable(null);
                config.httpProxy = in.readParcelable(null);
                return config;
            }

            public IpConfiguration[] newArray(int size) {
                return new IpConfiguration[size];
            }
        };
}
