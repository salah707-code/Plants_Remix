package com.example.plantencyclopedia.domain

import com.example.plantencyclopedia.data.Plant

data class ChemicalGroup(
    val chemicalName: String,
    val plants: List<Plant>,
    val frequency: Int
)

data class FamilyGroup(
    val familyName: String,
    val plants: List<Plant>,
    val count: Int
)

data class PlantEncyclopediaStats(
    val totalPlants: Int,
    val totalFamilies: Int,
    val totalFavorites: Int,
    val medicinalCount: Int,
    val culinaryCount: Int,
    val aromaticCount: Int,
    val cosmeticCount: Int,
    val topChemicals: List<ChemicalGroup>,
    val familyBreakdown: List<FamilyGroup>,
    val mostViewed: List<Plant>
)

object PlantAnalyticsUseCase {

    fun calculateStats(plants: List<Plant>): PlantEncyclopediaStats {
        val total = plants.size
        val favorites = plants.count { it.isFavorite }
        val medicinal = plants.count { it.usage.contains("علاج") }
        val culinary = plants.count { it.usage.contains("غذائ") }
        val aromatic = plants.count { it.usage.contains("عطر") }
        val cosmetic = plants.count { it.usage.contains("تجميل") }

        // Group by family
        val familyMap = plants.groupBy { it.family.ifBlank { "غير مصنفة" } }
        val familyBreakdown = familyMap.map { (fam, list) ->
            FamilyGroup(fam, list, list.size)
        }.sortedByDescending { it.count }

        // Group by chemicals
        val chemMap = mutableMapOf<String, MutableList<Plant>>()
        for (plant in plants) {
            for (chem in plant.chemicals) {
                val cleaned = chem.trim()
                if (cleaned.isNotBlank()) {
                    chemMap.getOrPut(cleaned) { mutableListOf() }.add(plant)
                }
            }
        }
        val topChemicals = chemMap.map { (chem, list) ->
            ChemicalGroup(chem, list, list.size)
        }.sortedByDescending { it.frequency }.take(8)

        val mostViewed = plants.sortedByDescending { it.viewCount }.take(5)

        return PlantEncyclopediaStats(
            totalPlants = total,
            totalFamilies = familyBreakdown.size,
            totalFavorites = favorites,
            medicinalCount = medicinal,
            culinaryCount = culinary,
            aromaticCount = aromatic,
            cosmeticCount = cosmetic,
            topChemicals = topChemicals,
            familyBreakdown = familyBreakdown,
            mostViewed = mostViewed
        )
    }

    fun findRelatedPlants(target: Plant, allPlants: List<Plant>): List<Pair<Plant, String>> {
        val related = mutableListOf<Pair<Plant, String>>()
        val targetChems = target.chemicals.map { it.trim().lowercase() }.toSet()

        for (other in allPlants) {
            if (other.id == target.id) continue

            val commonChems = other.chemicals.filter { targetChems.contains(it.trim().lowercase()) }
            if (commonChems.isNotEmpty()) {
                related.add(Pair(other, "مشترك في: ${commonChems.joinToString("، ")}"))
            } else if (other.family.isNotBlank() && other.family.equals(target.family, ignoreCase = true)) {
                related.add(Pair(other, "نفس الفصيلة: ${target.family}"))
            }
        }
        return related
    }
}
