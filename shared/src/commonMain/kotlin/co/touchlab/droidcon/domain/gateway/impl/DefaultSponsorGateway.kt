package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.service.impl.FirestoreApiDataSource

class DefaultSponsorGateway(
    private val firestoreApiDataSource: FirestoreApiDataSource,
): SponsorGateway {

    override suspend fun getSponsors(): List<SponsorGroup> =
        firestoreApiDataSource.getSponsors().groups.map { group ->
            SponsorGroup(
                name = (group.name.split('/').lastOrNull() ?: group.name)
                    .split(' ').joinToString(" ") { it.capitalize() },
                sponsors = group.fields.sponsors.arrayValue.values
                    .map { it.mapValue.fields }
                    .map { sponsorDto ->
                        Sponsor(
                            sponsorId = sponsorDto.sponsorId?.stringValue,
                            name = sponsorDto.name.stringValue,
                            icon = Url(sponsorDto.icon.stringValue),
                            url = Url(sponsorDto.url.stringValue),
                        )
                    },
                displayPriority = group.fields.displayOrder.integerValue.toInt(),
                isProminent = group.fields.prominent?.booleanValue ?: false,
            )
        }
}