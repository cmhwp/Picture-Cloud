/**
 * 图片审核状态
 */
export const PictureAuditStatusEnum = {
  PENDING: 0,
  APPROVED: 1,
  REJECTED: 2,
}
export const PictureAuditStatusEnumText = {
  0: '待审核',
  1: '审核通过',
  2: '审核不通过',
}
// 图片审核状态选项(根据数字找到对应的文字)
export const PictureReviewStatusOptions = Object.entries(PictureAuditStatusEnumText).map(
  ([key, value]) => ({
    value: key,
    label: value,
  }),
)

