package com.ubiqube.etsi.mano.repository;

import java.io.InputStream;
import java.nio.file.Path;

public interface ContentManager {

	void store(Path _filename, InputStream _stream);

	InputStream load(Path _filename, int i, Long object);

}
