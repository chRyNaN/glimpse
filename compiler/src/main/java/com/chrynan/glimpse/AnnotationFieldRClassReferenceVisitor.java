package com.chrynan.glimpse;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

/**
 * Created by ckeenan on 2/21/17. A {@link TreePathScanner} used to get a String representation of
 * an R class reference (ex: R.styleable.ViewClass_someField). To use this class call the
 * {@link #scan(TreePath, Object)} method providing a {@link TreePath} representation of the
 * annotation value containing the reference
 * (ex: trees.getPath(annotatedElement, annotationMirror, annotationValue); see {@link TreePath}).
 * The returned result of the scan method should be a String representation of the R class field or
 * null if none was found.
 */
class AnnotationFieldRClassReferenceVisitor extends TreePathScanner<String, Void> {

    private final String annotationFieldName;

    AnnotationFieldRClassReferenceVisitor(final String annotationFieldName){
        this.annotationFieldName = annotationFieldName;
    }

    @Override
    public String visitAnnotation(AnnotationTree node, Void p) {
        for (ExpressionTree expressionTree : node.getArguments()) {
            if (expressionTree instanceof AssignmentTree) {

                AssignmentTree assignmentTree = (AssignmentTree) expressionTree;
                ExpressionTree variable = assignmentTree.getVariable();

                if (variable instanceof IdentifierTree && ((IdentifierTree) variable).getName().contentEquals(annotationFieldName)) {
                    return scan(expressionTree, p);
                }
            }
        }

        return null;
    }

    @Override
    public String visitAssignment(AssignmentTree assignmentTree, Void p) {
        return assignmentTree.getExpression().toString();
    }
}
