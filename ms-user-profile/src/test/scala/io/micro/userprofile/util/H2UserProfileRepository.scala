package io.micro.userprofile.util

import io.micro.userprofile.dal.UserProfileRepository

object H2UserProfileRepository extends UserProfileRepository with H2TestDBComponent