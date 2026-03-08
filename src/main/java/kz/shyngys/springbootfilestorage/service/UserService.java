package kz.shyngys.springbootfilestorage.service;

import kz.shyngys.springbootfilestorage.dto.UserCreateRequest;
import kz.shyngys.springbootfilestorage.dto.UserResponse;
import kz.shyngys.springbootfilestorage.dto.UserUpdateRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    Mono<UserResponse> create(UserCreateRequest request);

    Mono<UserResponse> getById(Long id);

    Mono<List<UserResponse>> getAll();

    Mono<UserResponse> update(Long id, UserUpdateRequest request);

    Mono<Void> delete(Long id);

    Mono<UserResponse> getByUsername(String username);
}