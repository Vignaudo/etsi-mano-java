package com.ubiqube.etsi.mano.repository.msa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.api.interfaces.repository.RepositoryService;
import com.ubiqube.etsi.mano.exception.NotFoundException;
import com.ubiqube.etsi.mano.grammar.JsonBeanUtil;
import com.ubiqube.etsi.mano.grammar.JsonFilter;
import com.ubiqube.etsi.mano.model.lcmgrant.sol003.Grant;
import com.ubiqube.etsi.mano.model.nslcm.sol005.NsInstancesCreateNsRequest;
import com.ubiqube.etsi.mano.model.vnf.sol005.VnfPkgInfo;
import com.ubiqube.etsi.mano.repository.JndiWrapper;
import com.ubiqube.etsi.mano.repository.VnfPackageRepository;
import com.ubiqube.etsi.mano.repository.phys.VnfPackagePhys;
import com.ubiqube.etsi.mano.service.Configuration;
import com.ubiqube.etsi.mano.service.PropertiesConfiguration;
import com.ubiqube.etsi.mano.service.RepositoryServiceEjb;

public class VnfPackageGenTest {

	private final VnfPackageRepository vnfPackage;

	public VnfPackageGenTest() {
		final JsonFilter jsonFilter = new JsonFilter(new JsonBeanUtil());
		final ObjectMapper mapper = new ObjectMapper();
		final Configuration conf = new PropertiesConfiguration();
		final JndiWrapper _jndiWrapper = new JndiWrapper(conf);
		final RepositoryService _repositoryService = new RepositoryServiceEjb(_jndiWrapper);
		vnfPackage = new VnfPackagePhys(conf, mapper, jsonFilter, new LowMsa(_repositoryService));
		// vnfPackage = new VnfPackagePhys(conf, mapper, jsonFilter, new LowPhys());
	}

	@Test
	void testName() throws Exception {

		VnfPkgInfo entity = new VnfPkgInfo();
		vnfPackage.save(entity);
		assertNotNull(entity.getId());

		entity = vnfPackage.get(entity.getId());
		assertNotNull(entity);

		List<VnfPkgInfo> res = vnfPackage.query(null);
		assertNotNull(res);
		final int num = res.size();
		assertTrue(num >= 1);

		vnfPackage.delete(entity.getId());

		res = vnfPackage.query(null);
		assertNotNull(res);
		assertEquals(num - 1, res.size());
	}

	@Test
	void testNotFound() throws Exception {
		assertThrows(NotFoundException.class, () -> {
			vnfPackage.get("DEADBEEF");
		});
	}

	@Test
	public void testStoreError() {
		assertThrows(NotFoundException.class, () -> {
			vnfPackage.storeObject("BAD", "grant", new Grant());
		});
	}

	@Test
	public void testStoreScenario() {
		final VnfPkgInfo entity = new VnfPkgInfo();
		vnfPackage.save(entity);
		assertNotNull(entity.getId());

		vnfPackage.storeObject(entity.getId(), "grant", new Grant());
		vnfPackage.loadObject(entity.getId(), "grant", Grant.class);
		vnfPackage.delete(entity.getId());
	}

	@Test
	public void testLoadObjectError() {
		assertThrows(NotFoundException.class, () -> {
			vnfPackage.loadObject("BAD", "grant", NsInstancesCreateNsRequest.class);
		});
	}

	@Test
	public void testBinaryScenario() throws FileNotFoundException, NoSuchAlgorithmException {
		final VnfPkgInfo entity = new VnfPkgInfo();
		vnfPackage.save(entity);
		assertNotNull(entity.getId());

		final InputStream stream = new FileInputStream("src/test/resources/pack.zip");
		vnfPackage.storeBinary(entity.getId(), "file", stream);

		byte[] bytes = vnfPackage.getBinary(entity.getId(), "file");
		final MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(bytes);
		assertEquals("4d251f6f44b12f8e6a0b2e9e7e69e603", DatatypeConverter.printHexBinary(md5.digest()).toLowerCase());

		bytes = vnfPackage.getBinary(entity.getId(), "file", 0, Long.decode("2"));

		assertEquals(2, bytes.length);
		assertEquals('P', bytes[0]);
		assertEquals('K', bytes[1]);
		vnfPackage.delete(entity.getId());
	}

}
