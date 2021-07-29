package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Profile

interface ProfileRepository: Repository<Profile.Id, Profile>