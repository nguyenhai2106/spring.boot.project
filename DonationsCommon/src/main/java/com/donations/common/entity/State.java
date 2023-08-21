package com.donations.common.entity;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "states")
public class State extends IdBaseEntity {
	@Nationalized
	@Column(nullable = false, unique = true, length = 64)
	private String name;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	public State() {
	}

	public State(String name, Country country) {
		this.name = name;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "State [id = " + id + ", name = " + name + ", country = " + country + "]";
	}

}
