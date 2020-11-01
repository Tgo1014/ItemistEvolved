package com.jakmos.itemistevolved.persistence.manager

import androidx.lifecycle.LiveData
import com.jakmos.itemistevolved.persistence.database.dao.ChecklistDao
import com.jakmos.itemistevolved.persistence.database.dao.SubsectionDao
import com.jakmos.itemistevolved.persistence.database.entity.ChecklistEntity
import com.jakmos.itemistevolved.persistence.database.entity.ChecklistView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChecklistPersistenceManager @Inject constructor(
  private val checklistDao: ChecklistDao,
  private val subsectionDao: SubsectionDao
) {

  //region Observe

  fun observeChecklists(): LiveData<List<ChecklistView>> =
    checklistDao
      .observeChecklists()

  //endregion

  //region Save

  suspend fun saveChecklist(checklist: ChecklistEntity) {

    // Cleanup.
    checklistDao.remove(checklist)
    subsectionDao.remove(checklist.subsections)

    // Insert checklist.
    val checklistId = checklistDao.insert(checklist)

    // Add id to subsections.
    var subsections = checklist
      .subsections
      .map { it.apply { it.checklistId = checklistId } }

    // Add proper order to subsections.
    subsections = subsections.mapIndexed { index, entity ->
      entity.apply { orderNumber = index.toLong() }
    }

    // Insert subsections.
    subsectionDao.insert(subsections)
  }

  //endregion

  //region Remove

  suspend fun removeChecklist(checklist: ChecklistEntity) =
    checklistDao
      .remove(checklist)

  suspend fun removeChecklists(checklists: List<ChecklistEntity>) =
    checklistDao
      .remove(checklists)

  suspend fun clearTable() =
    checklistDao
      .clearTable()

  //endregion
}
