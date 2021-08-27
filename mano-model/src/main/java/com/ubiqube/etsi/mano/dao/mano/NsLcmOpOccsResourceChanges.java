package com.ubiqube.etsi.mano.dao.mano;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class NsLcmOpOccsResourceChanges implements Serializable {
	/** Serial. */
	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn
	private Set<NsInstantiatedVnf> affectedVnfs = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn
	private Set<NsInstantiatedPnf> affectedPnfs = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn
	private Set<NsInstantiatedVl> affectedVls = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn
	private Set<NsInstantiatedVnffg> affectedVnffgs = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn
	private Set<NsInstantiatedNs> affectedNss = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn
	private Set<NsInstantiatedSap> affectedSaps = new HashSet<>();

	public Set<NsInstantiatedVnf> getAffectedVnfs() {
		return affectedVnfs;
	}

	public void setAffectedVnfs(final Set<NsInstantiatedVnf> affectedVnfs) {
		this.affectedVnfs = affectedVnfs;
	}

	public Set<NsInstantiatedPnf> getAffectedPnfs() {
		return affectedPnfs;
	}

	public void setAffectedPnfs(final Set<NsInstantiatedPnf> affectedPnfs) {
		this.affectedPnfs = affectedPnfs;
	}

	public Set<NsInstantiatedVl> getAffectedVls() {
		return affectedVls;
	}

	public void setAffectedVls(final Set<NsInstantiatedVl> affectedVls) {
		this.affectedVls = affectedVls;
	}

	public Set<NsInstantiatedVnffg> getAffectedVnffgs() {
		return affectedVnffgs;
	}

	public void setAffectedVnffgs(final Set<NsInstantiatedVnffg> affectedVnffgs) {
		this.affectedVnffgs = affectedVnffgs;
	}

	public Set<NsInstantiatedNs> getAffectedNss() {
		return affectedNss;
	}

	public void setAffectedNss(final Set<NsInstantiatedNs> affectedNss) {
		this.affectedNss = affectedNss;
	}

	public Set<NsInstantiatedSap> getAffectedSaps() {
		return affectedSaps;
	}

	public void setAffectedSaps(final Set<NsInstantiatedSap> affectedSaps) {
		this.affectedSaps = affectedSaps;
	}

	public void addInstantiatedSap(final NsInstantiatedSap sap) {
		if (affectedSaps == null) {
			affectedSaps = new HashSet<>();
		}
		affectedSaps.add(sap);
	}

	public void addInstantiatedVirtualLink(final NsInstantiatedVl vl) {
		if (affectedVls == null) {
			affectedVls = new HashSet<>();
		}
		affectedVls.add(vl);
	}

	public void addInstantiatedNs(final NsInstantiatedNs nss) {
		if (affectedNss == null) {
			affectedNss = new HashSet<>();
		}
		affectedNss.add(nss);
	}

	public void addInstantiatedVnf(final NsInstantiatedVnf vnf) {
		if (affectedVnfs == null) {
			affectedVnfs = new HashSet<>();
		}
		affectedVnfs.add(vnf);
	}

}
