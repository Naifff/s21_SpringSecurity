package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// Extract provider (will be "github" in this case)
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// GitHub specific attribute extraction
		String oauthId = extractOAuthId(registrationId, attributes);
		String email = extractEmail(attributes);
		String name = extractName(attributes);
		String avatarUrl = extractAvatarUrl(attributes);

		log.debug("Loading OAuth2 user - Provider: {}, ID: {}, Email: {}", registrationId, oauthId, email);

		// Find existing user or create new one
		User user = userRepository.findByOauthId(oauthId)
				.orElseGet(() -> createNewUser(oauthId, email, name, registrationId, avatarUrl));

		// For security logging
		log.info("User {} authenticated via {}", email, registrationId);

		return new DefaultOAuth2User(
				user.getRole().equals("ROLE_ADMIN") ?
						AuthorityUtils.createAuthorityList("ROLE_ADMIN") :
						AuthorityUtils.createAuthorityList("ROLE_USER"),
				attributes,
				"login"  // GitHub uses "login" as the name attribute
		);
	}

	private String extractOAuthId(String registrationId, Map<String, Object> attributes) {
		if ("github".equals(registrationId)) {
			// GitHub uses numeric IDs, so we convert to String
			return String.valueOf(attributes.get("id"));
		}
		throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
	}

	private String extractEmail(Map<String, Object> attributes) {
		// GitHub might not provide email directly if it's private
		// In production, you might want to make an additional API call to get private emails
		return (String) attributes.get("email");
	}

	private String extractName(Map<String, Object> attributes) {
		// GitHub provides name, but might fall back to login if name is not set
		String name = (String) attributes.get("name");
		return name != null ? name : (String) attributes.get("login");
	}

	private String extractAvatarUrl(Map<String, Object> attributes) {
		return (String) attributes.get("avatar_url");
	}

	@Transactional
	protected User createNewUser(String oauthId, String email, String name,
								 String provider, String avatarUrl) {
		User newUser = User.builder()
				.oauthId(oauthId)
				.email(email)
				.name(name)
				.provider(provider)
				.role("ROLE_USER")
				.avatarUrl(avatarUrl)
				.enabled(true)
				.build();

		log.info("Creating new user: {}", email);
		return userRepository.save(newUser);
	}
}