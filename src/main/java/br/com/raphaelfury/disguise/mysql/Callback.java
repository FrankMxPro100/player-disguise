package br.com.raphaelfury.disguise.mysql;
/**
 * Copyright (C) Raphael Viana (Fury), all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public interface Callback<T> {

	/**
	 * Callback
	 */

	public void finish(T t);

}