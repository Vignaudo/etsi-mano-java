package com.ubiqube.etsi.mano.service.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ubiqube.etsi.mano.exception.GenericException;

import jakarta.annotation.Nullable;

public class MultiHashInputStream extends InputStream {

	private DigestInputStream inMd5;
	private DigestInputStream inSha1;
	private DigestInputStream inSha256;
	private DigestInputStream inSha512;

	public MultiHashInputStream(final InputStream is) {
		try {
			inMd5 = new DigestInputStream(is, MessageDigest.getInstance("md5"));
			inSha1 = new DigestInputStream(inMd5, MessageDigest.getInstance("SHA-1"));
			inSha256 = new DigestInputStream(inSha1, MessageDigest.getInstance("SHA-256"));
			inSha512 = new DigestInputStream(inSha256, MessageDigest.getInstance("SHA-512"));
		} catch (final NoSuchAlgorithmException e) {
			throw new GenericException(e);
		}
	}

	@Override
	public int read() throws IOException {
		return inSha512.read();
	}

	@Override
	public int read(final @Nullable byte[] b, final int off, final int len) throws IOException {
		return inSha512.read(b, off, len);
	}

	public String getMd5() {
		final byte[] bytes = inMd5.getMessageDigest().digest();
		return bytesToHex(bytes);
	}

	public String getSha1() {
		final byte[] bytes = inSha1.getMessageDigest().digest();
		return bytesToHex(bytes);
	}

	public String getSha256() {
		final byte[] bytes = inSha256.getMessageDigest().digest();
		return bytesToHex(bytes);
	}

	public String getSha512() {
		final byte[] bytes = inSha512.getMessageDigest().digest();
		return bytesToHex(bytes);
	}

	private static String bytesToHex(final byte[] hash) {
		return IntStream.range(0, hash.length)
				.mapToObj(x -> hash[x])
				.map(b -> String.format("%02X", b))
				.collect(Collectors.joining())
				.toLowerCase();
	}

}
