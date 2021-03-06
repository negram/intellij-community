// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.vcs.changes.actions

import com.intellij.openapi.actionSystem.ActionPlaces.CHANGES_VIEW_TOOLBAR
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.text.StringUtil.removeEllipsisSuffix
import com.intellij.openapi.vcs.VcsBundle.message
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.actions.isProjectUsesNonModalCommit
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.ui.ChangesListView
import com.intellij.openapi.vcs.changes.ui.RollbackChangesDialog
import com.intellij.util.ui.UIUtil.removeMnemonic
import com.intellij.vcsUtil.RollbackUtil.getRollbackOperationName
import kotlin.streams.toList

class RollbackFilesAction : DumbAwareAction() {
  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = false

    if (!e.isProjectUsesNonModalCommit()) return
    val project = e.project ?: return
    val changesView = e.getData(ChangesListView.DATA_KEY) ?: return
    val changes = changesView.selectedChanges.limit(2).toList()

    with(e.presentation) {
      isVisible = true
      isEnabled = changes.isNotEmpty()
      text = message("action.for.file.with.dialog.text", getRollbackOperationName(project), changes.size)
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    if (!checkClmActive(e)) return

    val project = e.project!!
    val changes = e.getData(VcsDataKeys.CHANGES)!!.toList()

    FileDocumentManager.getInstance().saveAllDocuments()
    RollbackChangesDialog.rollbackChanges(project, changes)
  }

  companion object {
    @JvmStatic
    fun checkClmActive(e: AnActionEvent): Boolean {
      val project = e.project ?: return false
      val title =
        if (CHANGES_VIEW_TOOLBAR == e.place) null
        else message("error.cant.perform.operation.now", removeEllipsisSuffix(removeMnemonic(getRollbackOperationName(project))))

      return !ChangeListManager.getInstance(project).isFreezedWithNotification(title)
    }
  }
}