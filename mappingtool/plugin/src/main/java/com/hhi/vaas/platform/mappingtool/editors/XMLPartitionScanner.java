/*******************************************************************************
 * Copyright (c) 2005 Prashant Deva.
 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License - v 1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package com.hhi.vaas.platform.mappingtool.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class XMLPartitionScanner extends RuleBasedPartitionScanner {
	public static final String XML_DEFAULT = "__xml_default";
	public static final String XML_COMMENT = "__xml_comment";
	public static final String XML_TAG = "__xml_tag";

	public XMLPartitionScanner() {

		IToken xmlComment = new Token(XML_COMMENT);
		IToken tag = new Token(XML_TAG);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new MultiLineRule("<!--", "-->", xmlComment);
		rules[1] = new TagRule(tag);

		setPredicateRules(rules);
	}
}
