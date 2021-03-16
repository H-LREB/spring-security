/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.oauth2.client;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2JwtBearerGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.util.Assert;

/**
 * A builder that builds a {@link DelegatingOAuth2AuthorizedClientProvider} composed of
 * one or more {@link OAuth2AuthorizedClientProvider}(s) that implement specific
 * authorization grants. The supported authorization grants are
 * {@link #authorizationCode() authorization_code}, {@link #refreshToken() refresh_token},
 * {@link #clientCredentials() client_credentials} and {@link #password() password}. In
 * addition to the standard authorization grants, an implementation of an extension grant
 * may be supplied via {@link #provider(OAuth2AuthorizedClientProvider)}.
 *
 * @author Joe Grandja
 * @since 5.2
 * @see OAuth2AuthorizedClientProvider
 * @see AuthorizationCodeOAuth2AuthorizedClientProvider
 * @see RefreshTokenOAuth2AuthorizedClientProvider
 * @see ClientCredentialsOAuth2AuthorizedClientProvider
 * @see PasswordOAuth2AuthorizedClientProvider
 * @see DelegatingOAuth2AuthorizedClientProvider
 */
public final class OAuth2AuthorizedClientProviderBuilder {

	private final Map<Class<?>, Builder> builders = new LinkedHashMap<>();

	private OAuth2AuthorizedClientProviderBuilder() {
	}

	/**
	 * Returns a new {@link OAuth2AuthorizedClientProviderBuilder} for configuring the
	 * supported authorization grant(s).
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public static OAuth2AuthorizedClientProviderBuilder builder() {
		return new OAuth2AuthorizedClientProviderBuilder();
	}

	/**
	 * Configures an {@link OAuth2AuthorizedClientProvider} to be composed with the
	 * {@link DelegatingOAuth2AuthorizedClientProvider}. This may be used for
	 * implementations of extension authorization grants.
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder provider(OAuth2AuthorizedClientProvider provider) {
		Assert.notNull(provider, "provider cannot be null");
		this.builders.computeIfAbsent(provider.getClass(), (k) -> () -> provider);
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code authorization_code} grant.
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder authorizationCode() {
		this.builders.computeIfAbsent(AuthorizationCodeOAuth2AuthorizedClientProvider.class,
				(k) -> new AuthorizationCodeGrantBuilder());
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code refresh_token} grant.
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder refreshToken() {
		this.builders.computeIfAbsent(RefreshTokenOAuth2AuthorizedClientProvider.class,
				(k) -> new RefreshTokenGrantBuilder());
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code refresh_token} grant.
	 * @param builderConsumer a {@code Consumer} of {@link RefreshTokenGrantBuilder} used
	 * for further configuration
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder refreshToken(Consumer<RefreshTokenGrantBuilder> builderConsumer) {
		RefreshTokenGrantBuilder builder = (RefreshTokenGrantBuilder) this.builders.computeIfAbsent(
				RefreshTokenOAuth2AuthorizedClientProvider.class, (k) -> new RefreshTokenGrantBuilder());
		builderConsumer.accept(builder);
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code client_credentials} grant.
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder clientCredentials() {
		this.builders.computeIfAbsent(ClientCredentialsOAuth2AuthorizedClientProvider.class,
				(k) -> new ClientCredentialsGrantBuilder());
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code client_credentials} grant.
	 * @param builderConsumer a {@code Consumer} of {@link ClientCredentialsGrantBuilder}
	 * used for further configuration
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder clientCredentials(
			Consumer<ClientCredentialsGrantBuilder> builderConsumer) {
		ClientCredentialsGrantBuilder builder = (ClientCredentialsGrantBuilder) this.builders.computeIfAbsent(
				ClientCredentialsOAuth2AuthorizedClientProvider.class, (k) -> new ClientCredentialsGrantBuilder());
		builderConsumer.accept(builder);
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code password} grant.
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder password() {
		this.builders.computeIfAbsent(PasswordOAuth2AuthorizedClientProvider.class, (k) -> new PasswordGrantBuilder());
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code password} grant.
	 * @param builderConsumer a {@code Consumer} of {@link PasswordGrantBuilder} used for
	 * further configuration
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder password(Consumer<PasswordGrantBuilder> builderConsumer) {
		PasswordGrantBuilder builder = (PasswordGrantBuilder) this.builders
				.computeIfAbsent(PasswordOAuth2AuthorizedClientProvider.class, (k) -> new PasswordGrantBuilder());
		builderConsumer.accept(builder);
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code jwt_bearer} grant.
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder jwtBearer() {
		this.builders.computeIfAbsent(JwtBearerOAuth2AuthorizedClientProvider.class,
				(k) -> new JwtBearerGrantBuilder());
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Configures support for the {@code jwt_bearer} grant.
	 * @param builderConsumer a {@code Consumer} of {@link JwtBearerGrantBuilder} used for
	 * further configuration
	 * @return the {@link OAuth2AuthorizedClientProviderBuilder}
	 */
	public OAuth2AuthorizedClientProviderBuilder jwtBearer(Consumer<JwtBearerGrantBuilder> builderConsumer) {
		JwtBearerGrantBuilder builder = (JwtBearerGrantBuilder) this.builders
				.computeIfAbsent(JwtBearerOAuth2AuthorizedClientProvider.class, (k) -> new JwtBearerGrantBuilder());
		builderConsumer.accept(builder);
		return OAuth2AuthorizedClientProviderBuilder.this;
	}

	/**
	 * Builds an instance of {@link DelegatingOAuth2AuthorizedClientProvider} composed of
	 * one or more {@link OAuth2AuthorizedClientProvider}(s).
	 * @return the {@link DelegatingOAuth2AuthorizedClientProvider}
	 */
	public OAuth2AuthorizedClientProvider build() {
		List<OAuth2AuthorizedClientProvider> authorizedClientProviders = new ArrayList<>();
		for (Builder builder : this.builders.values()) {
			authorizedClientProviders.add(builder.build());
		}
		return new DelegatingOAuth2AuthorizedClientProvider(authorizedClientProviders);
	}

	interface Builder {

		OAuth2AuthorizedClientProvider build();

	}

	/**
	 * A builder for the {@code password} grant.
	 */
	public final class PasswordGrantBuilder implements Builder {

		private OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> accessTokenResponseClient;

		private Duration clockSkew;

		private Clock clock;

		private PasswordGrantBuilder() {
		}

		/**
		 * Sets the client used when requesting an access token credential at the Token
		 * Endpoint.
		 * @param accessTokenResponseClient the client used when requesting an access
		 * token credential at the Token Endpoint
		 * @return the {@link PasswordGrantBuilder}
		 */
		public PasswordGrantBuilder accessTokenResponseClient(
				OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> accessTokenResponseClient) {
			this.accessTokenResponseClient = accessTokenResponseClient;
			return this;
		}

		/**
		 * Sets the maximum acceptable clock skew, which is used when checking the access
		 * token expiry. An access token is considered expired if it's before
		 * {@code Instant.now(this.clock) + clockSkew}.
		 * @param clockSkew the maximum acceptable clock skew
		 * @return the {@link PasswordGrantBuilder}
		 */
		public PasswordGrantBuilder clockSkew(Duration clockSkew) {
			this.clockSkew = clockSkew;
			return this;
		}

		/**
		 * Sets the {@link Clock} used in {@link Instant#now(Clock)} when checking the
		 * access token expiry.
		 * @param clock the clock
		 * @return the {@link PasswordGrantBuilder}
		 */
		public PasswordGrantBuilder clock(Clock clock) {
			this.clock = clock;
			return this;
		}

		/**
		 * Builds an instance of {@link PasswordOAuth2AuthorizedClientProvider}.
		 * @return the {@link PasswordOAuth2AuthorizedClientProvider}
		 */
		@Override
		public OAuth2AuthorizedClientProvider build() {
			PasswordOAuth2AuthorizedClientProvider authorizedClientProvider = new PasswordOAuth2AuthorizedClientProvider();
			if (this.accessTokenResponseClient != null) {
				authorizedClientProvider.setAccessTokenResponseClient(this.accessTokenResponseClient);
			}
			if (this.clockSkew != null) {
				authorizedClientProvider.setClockSkew(this.clockSkew);
			}
			if (this.clock != null) {
				authorizedClientProvider.setClock(this.clock);
			}
			return authorizedClientProvider;
		}

	}

	/**
	 * A builder for the {@code jwt_bearer} grant.
	 */
	public final class JwtBearerGrantBuilder implements Builder {

		private OAuth2AccessTokenResponseClient<OAuth2JwtBearerGrantRequest> accessTokenResponseClient;

		private Duration clockSkew;

		private Clock clock;

		private JwtBearerGrantBuilder() {
		}

		/**
		 * Sets the client used when requesting an access token credential at the Token
		 * Endpoint.
		 * @param accessTokenResponseClient the client used when requesting an access
		 * token credential at the Token Endpoint
		 * @return the {@link JwtBearerGrantBuilder}
		 */
		public JwtBearerGrantBuilder accessTokenResponseClient(
				OAuth2AccessTokenResponseClient<OAuth2JwtBearerGrantRequest> accessTokenResponseClient) {
			this.accessTokenResponseClient = accessTokenResponseClient;
			return this;
		}

		/**
		 * Sets the maximum acceptable clock skew, which is used when checking the access
		 * token expiry. An access token is considered expired if it's before
		 * {@code Instant.now(this.clock) + clockSkew}.
		 * @param clockSkew the maximum acceptable clock skew
		 * @return the {@link JwtBearerGrantBuilder}
		 */
		public JwtBearerGrantBuilder clockSkew(Duration clockSkew) {
			this.clockSkew = clockSkew;
			return this;
		}

		/**
		 * Sets the {@link Clock} used in {@link Instant#now(Clock)} when checking the
		 * access token expiry.
		 * @param clock the clock
		 * @return the {@link JwtBearerGrantBuilder}
		 */
		public JwtBearerGrantBuilder clock(Clock clock) {
			this.clock = clock;
			return this;
		}

		/**
		 * Builds an instance of {@link JwtBearerOAuth2AuthorizedClientProvider}.
		 * @return the {@link JwtBearerOAuth2AuthorizedClientProvider}
		 */
		@Override
		public OAuth2AuthorizedClientProvider build() {
			JwtBearerOAuth2AuthorizedClientProvider authorizedClientProvider = new JwtBearerOAuth2AuthorizedClientProvider();
			if (this.accessTokenResponseClient != null) {
				authorizedClientProvider.setAccessTokenResponseClient(this.accessTokenResponseClient);
			}
			if (this.clockSkew != null) {
				authorizedClientProvider.setClockSkew(this.clockSkew);
			}
			if (this.clock != null) {
				authorizedClientProvider.setClock(this.clock);
			}
			return authorizedClientProvider;
		}

	}

	/**
	 * A builder for the {@code client_credentials} grant.
	 */
	public final class ClientCredentialsGrantBuilder implements Builder {

		private OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient;

		private Duration clockSkew;

		private Clock clock;

		private ClientCredentialsGrantBuilder() {
		}

		/**
		 * Sets the client used when requesting an access token credential at the Token
		 * Endpoint.
		 * @param accessTokenResponseClient the client used when requesting an access
		 * token credential at the Token Endpoint
		 * @return the {@link ClientCredentialsGrantBuilder}
		 */
		public ClientCredentialsGrantBuilder accessTokenResponseClient(
				OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient) {
			this.accessTokenResponseClient = accessTokenResponseClient;
			return this;
		}

		/**
		 * Sets the maximum acceptable clock skew, which is used when checking the access
		 * token expiry. An access token is considered expired if it's before
		 * {@code Instant.now(this.clock) - clockSkew}.
		 * @param clockSkew the maximum acceptable clock skew
		 * @return the {@link ClientCredentialsGrantBuilder}
		 */
		public ClientCredentialsGrantBuilder clockSkew(Duration clockSkew) {
			this.clockSkew = clockSkew;
			return this;
		}

		/**
		 * Sets the {@link Clock} used in {@link Instant#now(Clock)} when checking the
		 * access token expiry.
		 * @param clock the clock
		 * @return the {@link ClientCredentialsGrantBuilder}
		 */
		public ClientCredentialsGrantBuilder clock(Clock clock) {
			this.clock = clock;
			return this;
		}

		/**
		 * Builds an instance of {@link ClientCredentialsOAuth2AuthorizedClientProvider}.
		 * @return the {@link ClientCredentialsOAuth2AuthorizedClientProvider}
		 */
		@Override
		public OAuth2AuthorizedClientProvider build() {
			ClientCredentialsOAuth2AuthorizedClientProvider authorizedClientProvider = new ClientCredentialsOAuth2AuthorizedClientProvider();
			if (this.accessTokenResponseClient != null) {
				authorizedClientProvider.setAccessTokenResponseClient(this.accessTokenResponseClient);
			}
			if (this.clockSkew != null) {
				authorizedClientProvider.setClockSkew(this.clockSkew);
			}
			if (this.clock != null) {
				authorizedClientProvider.setClock(this.clock);
			}
			return authorizedClientProvider;
		}

	}

	/**
	 * A builder for the {@code authorization_code} grant.
	 */
	public final class AuthorizationCodeGrantBuilder implements Builder {

		private AuthorizationCodeGrantBuilder() {
		}

		/**
		 * Builds an instance of {@link AuthorizationCodeOAuth2AuthorizedClientProvider}.
		 * @return the {@link AuthorizationCodeOAuth2AuthorizedClientProvider}
		 */
		@Override
		public OAuth2AuthorizedClientProvider build() {
			return new AuthorizationCodeOAuth2AuthorizedClientProvider();
		}

	}

	/**
	 * A builder for the {@code refresh_token} grant.
	 */
	public final class RefreshTokenGrantBuilder implements Builder {

		private OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient;

		private Duration clockSkew;

		private Clock clock;

		private RefreshTokenGrantBuilder() {
		}

		/**
		 * Sets the client used when requesting an access token credential at the Token
		 * Endpoint.
		 * @param accessTokenResponseClient the client used when requesting an access
		 * token credential at the Token Endpoint
		 * @return the {@link RefreshTokenGrantBuilder}
		 */
		public RefreshTokenGrantBuilder accessTokenResponseClient(
				OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient) {
			this.accessTokenResponseClient = accessTokenResponseClient;
			return this;
		}

		/**
		 * Sets the maximum acceptable clock skew, which is used when checking the access
		 * token expiry. An access token is considered expired if it's before
		 * {@code Instant.now(this.clock) - clockSkew}.
		 * @param clockSkew the maximum acceptable clock skew
		 * @return the {@link RefreshTokenGrantBuilder}
		 */
		public RefreshTokenGrantBuilder clockSkew(Duration clockSkew) {
			this.clockSkew = clockSkew;
			return this;
		}

		/**
		 * Sets the {@link Clock} used in {@link Instant#now(Clock)} when checking the
		 * access token expiry.
		 * @param clock the clock
		 * @return the {@link RefreshTokenGrantBuilder}
		 */
		public RefreshTokenGrantBuilder clock(Clock clock) {
			this.clock = clock;
			return this;
		}

		/**
		 * Builds an instance of {@link RefreshTokenOAuth2AuthorizedClientProvider}.
		 * @return the {@link RefreshTokenOAuth2AuthorizedClientProvider}
		 */
		@Override
		public OAuth2AuthorizedClientProvider build() {
			RefreshTokenOAuth2AuthorizedClientProvider authorizedClientProvider = new RefreshTokenOAuth2AuthorizedClientProvider();
			if (this.accessTokenResponseClient != null) {
				authorizedClientProvider.setAccessTokenResponseClient(this.accessTokenResponseClient);
			}
			if (this.clockSkew != null) {
				authorizedClientProvider.setClockSkew(this.clockSkew);
			}
			if (this.clock != null) {
				authorizedClientProvider.setClock(this.clock);
			}
			return authorizedClientProvider;
		}

	}

}
