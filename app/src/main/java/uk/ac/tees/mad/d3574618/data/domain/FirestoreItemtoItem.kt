package uk.ac.tees.mad.d3574618.data.domain

fun FirestoreItemResponse.toItem() = Item(
    id = key ?: "",
    name = item?.name ?: "",
    description = item?.description ?: "",
    keywords = item?.keywords ?: "",
    category = item?.category?.name ?: "",
    condition = item?.condition?.name ?: "",
    image = item?.image ?: emptyList(),
    swapRequests = item?.swapRequests ?: emptyList(),
    dateListed = item?.dateListed,
    listedBy = item?.listedBy,
    itemSwapStatus = item?.itemSwapStatus ?: ""
)

fun FirestoreItemResponse.FirestoreItem.toItem() = Item(
    id = id ?: "",
    name = name ?: "",
    description = description ?: "",
    keywords = keywords ?: "",
    category = category.name ?: "",
    condition = condition.name ?: "",
    image = image,
    swapRequests = swapRequests ?: emptyList(),
    dateListed = dateListed,
    listedBy = listedBy,
    itemSwapStatus = itemSwapStatus
)