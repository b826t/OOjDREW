// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2005 Marcel Ball
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

package org.ruleml.oojdrew.util;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import nu.xom.Document;
import nu.xom.Element;

import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;

/**
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.89
 */
public class DefiniteClause {

    /**
     * Contains the Term objects that represent the Atoms of the clause. The
     * term at index 0 is the head of the clause. The atoms at indicies 1 .. n-1
     * are the Atoms of the body of the clause if they exist.
     */
    public Term[] atoms;

    /**
     * Contains the variable names for this clause. Since OO jDREW uses local
     * shallow binding the variables have a local scope and are stored with the
     * clause instead of in the global symbol table.
     */
    public String[] variableNames;

    /**
     * Creates a new clause representation using the a Vector of atoms and a
     * Vector of variable names. This constructor assumes that the types for a
     * variable are correct (i.e. a variable doesn't have a type that doesn't
     * exist and a variable doesn't have two different types at two places in
     * the clause).
     *
     * A definite Clause Needs A Vector Of Term Objects that represent The atoms
     * of the clause to be constructed.  The first object(Index 0) is the head
     * of the clause, while any remaining objects are the body atoms (conjunctivley)joined
     * The VariableNames vector contains the names for the variable names for
     * the clause to be constructed.
     *
     * @param atoms Vector A vector of Term objects that represent the Atoms of
     * the clause to be constructed. The first object (index 0) is the head of
     * the clause, while any remaining objects are the body atoms
     * (conjunctively) joined.
     *
     * @param variableNames Vector A vector containing the variable names for
     * the clause to be constructed.
     */
     
    public DefiniteClause(Vector atoms, Vector variableNames) {
        this.atoms = new Term[atoms.size()];
        for (int i = 0; i < this.atoms.length; i++) {
            this.atoms[i] = (Term) atoms.get(i);
        }

        this.variableNames = new String[variableNames.size()];
        for (int i = 0; i < this.variableNames.length; i++) {
            this.variableNames[i] = (String) variableNames.get(i);
        }
    }
    /**
     * Creates and returns a string representation of the clause in either POSL
     * or RuleML format. The format is determined by the org.ruleml.oojdrew.Config.PRPRINT
     * variable. If PRPRINT is true - then the POSL format is used for output,
     * otherwise RuleML (0.88 +) is used.
     *
     * @return String The string representation of the clause.
     */
     public String toString() {
        if (org.ruleml.oojdrew.Config.PRPRINT) {
            return toPOSLString();
        } else {
            return toRuleMLString(RuleMLFormat.RuleML88);
        }
     }      

    /**
     * Creates and returns a string representation of the clause in either POSL
     * or RuleML format. The format is determined by the org.ruleml.oojdrew.Config.PRPRINT
     * variable. If PRPRINT is true - then the POSL format is used for output,
     * otherwise RuleML (0.88 + rests or 0.91) is used.
     *  
     * @param version RuleMLVersion - this is to determine what RuleML parser to use.
     *
     * @return String The string representation of the clause.
     */
          
    public String toString(RuleMLFormat version) {
        if (org.ruleml.oojdrew.Config.PRPRINT) {
            return toPOSLString();
        } else {
            return toRuleMLString(version);
        }
    }

    /**
     * Builds and returns a POSL string representation of the clause. This is
     * used by the toString() method if the org.ruleml.oojdrew.Config.PRPRINT variable is
     * true, and can also be called directly by the user code.
     *
     * @return String The POSL representation of the clause stored in a Java
     * String object.
     */
    public String toPOSLString() {
        String s = "";
        s += atoms[0].toPOSLString(variableNames, true);

        if (atoms.length > 1) {
            s += " :- ";
            for (int i = 1; i < atoms.length; i++) {
                s += atoms[i].toPOSLString(variableNames, false) + ", ";
            }

            s = s.substring(0, s.length() - 2);
        }

        s += ".";

        return s;
    }

    /**
     * Builds and returns a RuleML (0.88 + rests or 0.91) string representation of the
     * object. The output is "pretty printed" with indentation set to 3 spaces
     * and new-lines enabled. "\n" is used for newlines, therefore the output
     * may not appear properly on MS Windows based operating systems.
     *
     * This method is called by the toString() method if org.ruleml.oojdrew.Config.PRPRINT
     * is false, and can be called by user code.
     *
     * @param version RuleMLVersion - this is to determine what RuleML parser to use.
     *
     * @return String The RuleML representation stored in a Java String object.
     */
     
    public String toRuleMLString(RuleMLFormat version) {
        Element rml = this.toRuleML(version);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        nu.xom.Serializer sl = new nu.xom.Serializer(os);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
        	Document doc = new Document(rml);
            sl.write(doc);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return os.toString();
    }

    /**
     * Builds and returns a RuleML (0.88 + rests or 0.91) representation of the object
     * as a XOM tree. Users can then manipulate this object using regular calls
     * to the XOM library routines.
     *
     * This method is called by the toRuleMLString() method, and can be called
     * by user code.
     *
     * @param version int - this is to determine what RuleML parser to use.
     *
     * @return Element The RuleML representation of the clause stored as a XOM
     * tree.
     */
          
    public Element toRuleML(RuleMLFormat version) {
		
        Element el;
        if (atoms.length == 1) {

            el = atoms[0].toRuleML(variableNames, true, version);
        } else {
            el = new Element("Implies");
            if (atoms.length > 2) {
                Element el2 = new Element("And");
                for (int i = 1; i < atoms.length; i++) {

                    el2.appendChild(atoms[i].toRuleML(variableNames, false, version));
                }
                el.appendChild(el2);
            } else {
                el.appendChild(atoms[1].toRuleML(variableNames, false, version));
            }

            el.appendChild(atoms[0].toRuleML(variableNames, true, version));

        }
        //Each atom no longer needs a closure attribute, the closure is
        //defined at the Rulebase level
        //Attribute a = new Attribute("closure", "universal");
        //el.addAttribute(a);

        return el;
    }

    /**
     * Tests to see if the clause represented by this clause is a fact - this
     * is determined by checking to see if the clause only has one atom (the
     * head).
     *
     * @return boolean Returns true if the clause represents a fact, false
     * otherwise.
     */
    public boolean isFact() {
        return (atoms.length == 1);
    }

}
