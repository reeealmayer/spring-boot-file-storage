package kz.shyngys.springbootfilestorage.service.impl;

import kz.shyngys.springbootfilestorage.model.SecretUser;
import kz.shyngys.springbootfilestorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() ->
                        userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User with " + username + " not found"))
                ).map(SecretUser::fromUser)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
