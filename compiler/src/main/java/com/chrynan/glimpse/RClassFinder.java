package com.chrynan.glimpse;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import javax.lang.model.type.TypeMirror;

/**
 * Created by ckeenan on 2/22/17. A {@link TreeScanner} used to get the full String representation
 * of a R class name, including the package (ex: com.company.app.R). This can be used on a
 * {@link JCTree} via its "accept" method. Then the caller can use this class'
 * {@link #getClassName()} to get the String name of the R class (null if it was not available).
 * This can be used to either create a {@link com.squareup.javapoet.TypeName} of the class or
 * to retrieve it as an element (ex: {@link javax.lang.model.util.Elements#getTypeElement(CharSequence)},
 * {@link javax.lang.model.util.Types#asElement(TypeMirror)}, etc).
 */
class RClassFinder extends TreeScanner {
    private String className;

    @Override
    public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
        final Symbol symbol = jcFieldAccess.sym;

        if (symbol != null
                && symbol.getEnclosingElement() != null
                && symbol.getEnclosingElement().getEnclosingElement() != null
                && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
            className = symbol.getEnclosingElement().getEnclosingElement().enclClass().className();
        }
    }

    String getClassName() {
        return className;
    }
}
