package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.SponsorGroup

interface SponsorGroupRepository: Repository<SponsorGroup.Id, SponsorGroup>{
    fun allSync(): List<SponsorGroup>
}