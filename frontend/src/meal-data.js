export const pantryIngredients = [
  '김치',
  '두부',
  '대파',
  '밥',
  '달걀',
  '간장',
  '마늘',
  '다진마늘',
  '참기름',
  '설탕',
  '소금',
  '후추',
]

export const ingredientCatalog = {
  김치: { amount: '200g', packageLabel: '1kg', packageCost: 4800, weight: 16 },
  돼지고기: { amount: '150g', packageLabel: '500g', packageCost: 8500, weight: 30 },
  두부: { amount: '1/2모', packageLabel: '1모', packageCost: 1800, weight: 10 },
  대파: { amount: '1/3대', packageLabel: '1단', packageCost: 1900, weight: 5 },
  양파: { amount: '1/4개', packageLabel: '1개', packageCost: 800, weight: 5 },
  고춧가루: { amount: '1T', packageLabel: '100g', packageCost: 3500, weight: 4 },
  다진마늘: { amount: '1T', packageLabel: '100g', packageCost: 3500, weight: 4 },
  국간장: { amount: '1T', packageLabel: '500ml', packageCost: 3900, weight: 3 },
  참치캔: { amount: '1/2캔', packageLabel: '1캔', packageCost: 1800, weight: 24 },
  달걀: { amount: '1개', packageLabel: '10구', packageCost: 4500, weight: 15 },
  밥: { amount: '1공기', packageLabel: '쌀 1kg', packageCost: 6000, weight: 14 },
  마요네즈: { amount: '1T', packageLabel: '300g', packageCost: 3500, weight: 7 },
  김가루: { amount: '1T', packageLabel: '50g', packageCost: 2500, weight: 4 },
  된장: { amount: '1T', packageLabel: '500g', packageCost: 5000, weight: 15 },
  애호박: { amount: '1/4개', packageLabel: '1개', packageCost: 1500, weight: 12 },
  감자: { amount: '1/2개', packageLabel: '3개 묶음', packageCost: 3000, weight: 13 },
  고추장: { amount: '1T', packageLabel: '500g', packageCost: 5000, weight: 10 },
  마늘: { amount: '3쪽', packageLabel: '한 줌', packageCost: 2500, weight: 4 },
  간장: { amount: '1T', packageLabel: '500ml', packageCost: 3900, weight: 4 },
  설탕: { amount: '1t', packageLabel: '1kg', packageCost: 2500, weight: 3 },
  참기름: { amount: '1t', packageLabel: '160ml', packageCost: 6500, weight: 4 },
  파스타면: { amount: '160g', packageLabel: '500g', packageCost: 3000, weight: 28 },
  토마토소스: { amount: '200g', packageLabel: '400g', packageCost: 4500, weight: 30 },
  올리브유: { amount: '1T', packageLabel: '250ml', packageCost: 7500, weight: 8 },
  파마산치즈: { amount: '10g', packageLabel: '80g', packageCost: 5000, weight: 10 },
  바질: { amount: '약간', packageLabel: '건조 바질 10g', packageCost: 2500, weight: 4 },
  닭가슴살: { amount: '100g', packageLabel: '100g 3팩', packageCost: 6000, weight: 24 },
  양상추: { amount: '1/4통', packageLabel: '1통', packageCost: 2800, weight: 20 },
  방울토마토: { amount: '5개', packageLabel: '500g', packageCost: 5500, weight: 18 },
  드레싱: { amount: '2T', packageLabel: '250ml', packageCost: 4500, weight: 10 },
  오이: { amount: '1/3개', packageLabel: '1개', packageCost: 1000, weight: 12 },
  옥수수: { amount: '2T', packageLabel: '캔 1개', packageCost: 2200, weight: 8 },
}

export function calculateMealEstimate(menu, servings, ownedIngredients) {
  const portionRatio = servings / menu.servings
  const ownedSet = new Set(ownedIngredients)
  const rawIngredients = menu.ingredients.map((name) => ({
    name,
    ...(ingredientCatalog[name] || { amount: '적정량', packageLabel: '1개', packageCost: 1500, weight: 5 }),
    owned: ownedSet.has(name),
  }))
  const totalWeight = rawIngredients.reduce((sum, ingredient) => sum + ingredient.weight, 0) || 1
  const cookingCost = Math.round(menu.homeCost * portionRatio)
  const dineOutCost = Math.round(menu.dineOutCost * portionRatio)
  const packageMultiplier = Math.max(1, Math.ceil(portionRatio))
  const ingredients = rawIngredients.map((ingredient) => ({
    ...ingredient,
    usageCost: Math.round(cookingCost * ingredient.weight / totalWeight),
    purchaseCost: ingredient.owned ? 0 : ingredient.packageCost * packageMultiplier,
  }))
  const shoppingCost = ingredients.reduce((sum, ingredient) => sum + ingredient.purchaseCost, 0)

  return {
    ingredients,
    portionRatio,
    ownedCount: ingredients.filter((ingredient) => ingredient.owned).length,
    cookingCost,
    shoppingCost,
    dineOutCost,
    savings: dineOutCost - cookingCost,
  }
}
