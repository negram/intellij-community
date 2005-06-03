package com.siyeh.ipp.trivialif;

import com.intellij.psi.*;
import com.siyeh.ipp.base.PsiElementPredicate;
import com.siyeh.ipp.psiutils.ErrorUtil;

class MergeElseIfPredicate implements PsiElementPredicate{
    public boolean satisfiedBy(PsiElement element){
        if(!(element instanceof PsiJavaToken)){
            return false;
        }
        final String text = element.getText();
        if(!"else".equals(text)){
            return false;
        }
        final PsiJavaToken token = (PsiJavaToken) element;

        final PsiElement parent = token.getParent();
        if(!(parent instanceof PsiIfStatement)){
            return false;
        }
        final PsiIfStatement ifStatement = (PsiIfStatement) parent;
        if(ErrorUtil.containsError(ifStatement)){
            return false;
        }
        final PsiStatement thenBranch = ifStatement.getThenBranch();
        final PsiStatement elseBranch = ifStatement.getElseBranch();
        if(thenBranch == null){
            return false;
        }
        if(elseBranch == null){
            return false;
        }
        if(!(elseBranch instanceof PsiBlockStatement)){
            return false;
        }
        final PsiCodeBlock block =
                ((PsiBlockStatement) elseBranch).getCodeBlock();
        final PsiStatement[] statements = block.getStatements();
        if(statements == null || statements.length != 1){
            return false;
        }
        return statements[0] != null && statements[0] instanceof PsiIfStatement;
    }
}
