package com.ubiqube.etsi.mano.repository.phys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.etsi.mano.exception.GenericException;
import com.ubiqube.etsi.mano.exception.NotFoundException;
import com.ubiqube.etsi.mano.grammar.AstBuilder;
import com.ubiqube.etsi.mano.grammar.JsonFilter;
import com.ubiqube.etsi.mano.repository.BinaryRepository;
import com.ubiqube.etsi.mano.repository.CrudRepository;
import com.ubiqube.etsi.mano.repository.Low;

/**
 *
 * @author Olivier
 *
 */
public abstract class GenaricBinaryRepository<T> implements CrudRepository<T>, BinaryRepository {
	private static final Logger LOG = LoggerFactory.getLogger(GenaricBinaryRepository.class);

	private final String root;
	private final ObjectMapper objectMapper;
	private final JsonFilter jsonFilter;
	private final Low lowDriver;

	public GenaricBinaryRepository(final String _root, final ObjectMapper _objectMapper, final JsonFilter _jsonFilter, final Low _lowDriver) {
		root = _root;
		new File(_root).mkdirs();
		objectMapper = _objectMapper;
		jsonFilter = _jsonFilter;
		lowDriver = _lowDriver;
		LOG.info("Physival backend at: {}", _root);
	}

	@Override
	public final List<T> query(final String filter) {
		final List<String> listFilesInFolder = lowDriver.find(Paths.get(root, getDir()).toString(), getFilename());
		final AstBuilder astBuilder = new AstBuilder(filter);
		return listFilesInFolder.stream()
				.map(this::rawGetObject)
				.filter(x -> jsonFilter.apply(x, astBuilder))
				.collect(Collectors.toList());
	}

	@Override
	public final T get(final String _id) {
		Path path = getRoot(_id);
		verifyPath(path);
		path = combine(path, getFilename());
		try {
			final byte[] content = lowDriver.get(path.toString());
			return objectMapper.readValue(content, getClazz());
		} catch (final IOException e) {
			throw new GenericException(e);
		}
	}

	@Override
	public final void delete(final String _id) {
		final Path path = getRoot(_id);
		verifyPath(path);
		lowDriver.delete(path.toString());
	}

	@Override
	public final T save(final T entity) {
		final String id = setId(entity);
		final Path path = getRoot(id);
		lowDriver.mkdir(path.toString());
		storeObject(id, getFilename(), entity);
		return entity;
	}

	@Override
	public final void storeObject(final String _id, final String _filename, final Object _object) {
		Path path = getRoot(_id);
		verifyPath(path);
		try {
			final String content = objectMapper.writeValueAsString(_object);
			path = combine(path, _filename);
			lowDriver.add(path.toString(), content.getBytes());
		} catch (final IOException e) {
			throw new GenericException(e);
		}
	}

	@Override
	public final void storeBinary(final String _id, final String _filename, final InputStream _stream) {
		Path path = getRoot(_id);
		verifyPath(path);
		path = combine(path, _filename);
		lowDriver.add(path.toString(), _stream);
	}

	@Override
	public final byte[] getBinary(final String _id, final String _filename) {
		Path path = getRoot(_id);
		path = combine(path, _filename);
		verifyPath(path);
		return lowDriver.get(path.toString());
	}

	@Override
	public final byte[] getBinary(final String _id, final String _filename, final int min, final Long max) {
		Path path = getRoot(_id);
		path = combine(path, _filename);
		verifyPath(path);
		return lowDriver.get(path.toString(), min, max);
	}

	@Override
	public <T, U extends Class> T loadObject(@NotNull final String _id, @NotNull final String _filename, final U t) {
		final byte[] bytes = getBinary(_id, _filename);
		try {
			return (T) objectMapper.readValue(bytes, t);
		} catch (final IOException e) {
			throw new GenericException(e);
		}
	}

	protected abstract String setId(T entity);

	protected abstract Class<T> getClazz();

	protected abstract String getFilename();

	protected abstract String getDir();

	protected final static String sanitize(final String filename) {
		// It's ok for path segment not for a full path.
		return filename.replaceAll("\\.+", ".");
	}

	private void verifyPath(final Path path) {
		if (!lowDriver.exist(path.toString())) {
			throw new NotFoundException("Unable to find " + path);
		}
	}

	private Path getRoot(final String _id) {
		return Paths.get(root, getDir(), sanitize(_id));
	}

	private static Path combine(final Path path, final String _filename) {
		return Paths.get(path.toString(), sanitize(_filename));
	}

	private T rawGetObject(final String _path) {
		final byte[] bytes = lowDriver.get(_path);
		try {
			return objectMapper.readValue(bytes, getClazz());
		} catch (final IOException e) {
			throw new GenericException(e);
		}
	}
}
