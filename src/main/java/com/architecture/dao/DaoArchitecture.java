package com.architecture.dao;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ------------------------------------------------------
// MODEL
// ------------------------------------------------------
record User(int id, String name) { }

// ------------------------------------------------------
// REPOSITORY CONTRACT (DAO interface)
// ------------------------------------------------------
interface UserRepository {
	Optional<User> findById(int id);
	List<User> findAll();
	void save(User user);
	void delete(int id);
}

// ------------------------------------------------------
// MAP implementation
// ------------------------------------------------------
class InMemoryMapUserRepository implements UserRepository {

	private final Map<Integer, User> storage = new HashMap<>();

	@Override
	public void save(User user) {
		storage.put(user.id(), user);
	}

	@Override
	public Optional<User> findById(int id) {
		return Optional.ofNullable(storage.get(id));
	}

	@Override
	public List<User> findAll() {
		return new ArrayList<>(storage.values());
	}

	@Override
	public void delete(int id) {
		storage.remove(id);
	}
}

// ------------------------------------------------------
// LIST implementation
// ------------------------------------------------------
class InMemoryListUserRepository implements UserRepository {

	private final List<User> storage = new ArrayList<>();

	@Override
	public void save(User user) {
		storage.removeIf(u -> u.id() == user.id());
		storage.add(user);
	}

	@Override
	public Optional<User> findById(int id) {
		return storage.stream()
				.filter(u -> u.id() == id)
				.findFirst();
	}

	@Override
	public List<User> findAll() {
		return new ArrayList<>(storage);
	}

	@Override
	public void delete(int id) {
		storage.removeIf(u -> u.id() == id);
	}
}

// ------------------------------------------------------
// SPRING BOOT APPLICATION + DEMO EXECUTION
// ------------------------------------------------------
@SpringBootApplication
public class DaoArchitecture {
	public static void main(String[] args) {
		SpringApplication.run(DaoArchitecture.class, args);

		new DaoArchitecture().demo();
	}

	void demo() {

		UserRepository repo1 = new InMemoryMapUserRepository();
		UserRepository repo2 = new InMemoryListUserRepository();

		AtomicInteger seq = new AtomicInteger(0);

		repo1.save(new User(seq.incrementAndGet(), "jack"));
		repo2.save(new User(seq.get(), "jack"));

		System.out.println("MAP:  " + repo1.findById(1));
		System.out.println("LIST: " + repo2.findById(1));
	}

}
