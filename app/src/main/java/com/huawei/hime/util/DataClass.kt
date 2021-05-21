package com.huawei.hime.util

data class UploadTravelDataClass(
    val commentsAllowed : String = "",
    val upload_imageUrl : String = "",
    val popularity : String = "",
    val uploadType : String = "",
    val upload_date : String = "",
    val upload_footer : String = "",
    val upload_lovely : String = "",
    val upload_posterImage : String = "",
    val upload_posterName : String = "",
    val upload_travel_address : String = "",
    val upload_travel_country : String = "",
    val upload_travel_latitude : String = "",
    val upload_travel_longitude : String = "",
    val upload_millis : String = "",
    val uploaderID : String = "",
    val typeGroup : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = ""
)

data class UploadImageDataClass(
    val commentsAllowed : String = "",
    val popularity : String = "",
    val uploadType : String = "",
    val upload_footer : String = "",
    val upload_imageUrl : String = "",
    val upload_lovely : String = "",
    val upload_posterImage : String = "",
    val upload_posterName : String = "",
    val upload_date : String = "",
    val upload_millis : String = "",
    val uploaderID : String = "",
    val typeGroup : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = ""
)

data class UploadVideoDataClass(
    val commentsAllowed : String = "",
    val popularity : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = "",
    val typeGroup : String = "",
    val upload_millis : String = "",
    val uploadType : String = "",
    val upload_date : String = "",
    val upload_footer : String = "",
    val upload_lovely : String = "",
    val upload_posterImage : String = "",
    val upload_posterName : String = "",
    val upload_videoTime : String = "",
    val upload_videoUrl : String = "",
    val uploaderID : String = "",
    val uploader_interests : String = ""
)

data class UploadEventDataClass(
    val commentsAllowed : String = "",
    val popularity : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = "",
    val typeGroup : String = "",
    val upload_millis : String = "",
    val uploadType : String = "",
    val upload_date : String = "",
    val upload_footer : String = "",
    val upload_lovely : String = "",
    val upload_posterImage : String = "",
    val upload_posterName : String = "",
    val upload_event_time : String = "",
    val upload_event_photoUrl : String = "",
    val upload_event_end_time : String = "",
    val upload_event_start_time : String = "",
    val upload_event_coverUrl : String = "",
    val upload_event_interest : String = "",
    val upload_event_picked0 : String = "",
    val upload_event_picked1 : String = "",
    val upload_event_picked2 : String = "",
    val upload_event_picked3 : String = "",
    val upload_event_picked4 : String = "",
    val uploaderID : String = "",
    val uploader_interests : String = ""
)

/* All Upload Values*/
data class HybridUploadDataClass(
    val commentsAllowed : String = "",
    val popularity : String = "",
    val uploadType : String = "",
    val upload_videoTime : String = "",
    val upload_videoUrl : String = "",
    val upload_travel_address : String = "",
    val upload_travel_country : String = "",
    val upload_travel_latitude : String = "",
    val upload_travel_longitude : String = "",
    val upload_footer : String = "",
    val upload_imageUrl : String = "",
    val upload_lovely : String = "",
    val upload_posterImage : String = "",
    val upload_posterName : String = "",
    val upload_date : String = "",
    val uploaderID : String = "",
    val typeGroup : String = "",
    val upload_event_time : String = "",
    val upload_event_photoUrl : String = "",
    val upload_event_coverUrl : String = "",
    val upload_event_end_time : String = "",
    val upload_event_start_time : String = "",
    val upload_event_interest : String = "",
    val upload_event_picked0 : String = "",
    val upload_event_picked1 : String = "",
    val upload_event_picked2 : String = "",
    val upload_event_picked3 : String = "",
    val upload_event_picked4 : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = ""
)

data class ProfilePhotosDataClass(
    val footer : String = "",
    val photos_lovely : String = "",
    val photos_photoUrl : String = "",
    val photo_uploadTime : String = "",
    val commentsAllowed : String = "",
    val shareStat : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = ""
)

data class ProfileEventDataClass(
    val commentsAllowed : String = "",
    val event_coverUrl : String = "",
    val event_end_time : String = "",
    val event_interest : String = "",
    val event_lovely : String = "",
    val event_photoUrl : String = "",
    val event_picked0 : String = "",
    val event_picked1 : String = "",
    val event_picked2 : String = "",
    val event_picked3 : String = "",
    val event_picked4 : String = "",
    val event_picked5 : String = "",
    val event_start_time : String = "",
    val event_time : String = "",
    val footer : String = "",
    val shareStat : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = "",
    val upload_date : String = "",
    val upload_millis : String = ""
)

data class ProfilePicturesDataClass(
    val footer : String = "",
    val profile_lovely : String = "",
    val profile_photoUrl : String = "",
    val profile_thumbUrl : String = "",
    val profile_uploadTime : String = "",
    val photos_uploadMillis : String = "",
    val commentsAllowed : String = "",
    val shareStat : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = ""
)

data class ProfileTravelDataClass(
    val commentsAllowed : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = "",
    val travel_address : String = "",
    val travel_date : String = "",
    val footer : String = "",
    val travel_latitude : String = "",
    val travel_longitude : String = "",
    val travel_map_holder : String = "",
    val travel_map_lovely : String = ""
)

data class ProfileVideosDataClass(
    val commentsAllowed : String = "",
    val shareStat : String = "",
    val tag1 : String = "",
    val tag2 : String = "",
    val tag3 : String = "",
    val tag4 : String = "",
    val tag5 : String = "",
    val footer : String = "",
    val video_lovely : String = "",
    val video_uploadTime : String = "",
    val video_videoUrl : String = ""
)

data class FollowDataClass(
    val age : String = "",
    val following_since : String = "",
    val image : String = "",
    val lovely : String = "",
    val name : String = ""
)

data class UserInfoDataClass(
    val nameSurname : String = "",
    var thumbUrl : String = "",
    val photoUrl : String = "",
    val age : String = "",
    val lovely : String = ""
)

data class PostMessageDataClass(
    val comment : String = "",
    val comment_lovely : String = "",
    val commenter_ID : String = "",
    val commenter_image : String = "",
    val commenter_name : String = "",
    val time : String = "",
    val type : String = ""
)