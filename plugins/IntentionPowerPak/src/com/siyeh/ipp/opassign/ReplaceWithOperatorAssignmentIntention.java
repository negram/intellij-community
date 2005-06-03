package com.siyeh.ipp.opassign;

import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.ipp.base.MutablyNamedIntention;
import com.siyeh.ipp.base.PsiElementPredicate;

public class ReplaceWithOperatorAssignmentIntention
        extends MutablyNamedIntention{
    public String getTextForElement(PsiElement element){
        final PsiAssignmentExpression exp = (PsiAssignmentExpression) element;
        final PsiBinaryExpression rhs =
                (PsiBinaryExpression) exp.getRExpression();
        assert rhs != null;
        final PsiJavaToken sign = rhs.getOperationSign();
        final String operator = sign.getText();
        return "Replace = with " + operator + '=';
    }

    public String getFamilyName(){
        return "Replace Assignment With Operator Assignment";
    }

    public PsiElementPredicate getElementPredicate(){
        return new AssignmentExpressionReplaceableWithOperatorAssigment();
    }

    public void processIntention(PsiElement element)
            throws IncorrectOperationException{
        final PsiAssignmentExpression exp = (PsiAssignmentExpression) element;
        final PsiBinaryExpression rhs =
                (PsiBinaryExpression) exp.getRExpression();
        final PsiExpression lhs = exp.getLExpression();
        assert rhs != null;
        final PsiJavaToken sign = rhs.getOperationSign();
        final String operand = sign.getText();
        final PsiExpression rhsrhs = rhs.getROperand();
        assert rhsrhs != null;
        final String expString =
                lhs.getText() + operand + '=' + rhsrhs.getText();
        replaceExpression(expString, exp);
    }
}
