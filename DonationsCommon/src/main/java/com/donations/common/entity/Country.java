package com.donations.common.entity;

import java.util.Set;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class Country extends IdBaseEntity {
	@Nationalized
	@Column(nullable = false, unique = true, length = 128)
	private String name;

	@Column(nullable = false, unique = true, length = 8)
	private String code;

	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
	private Set<State> states;

	public Country() {
	}

	public Country(Integer id) {
		this.id = id;
	}

	public Country(Integer id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public Country(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public Country(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	public Set<State> getStates() {
//		return states;
//	}
//
//	public void setStateas(Set<State> states) {
//		this.states = states;
//	}

	@Override
	public String toString() {
		return "Country [id = " + id + ", name = " + name + ", code = " + code + "]";
	}

}
