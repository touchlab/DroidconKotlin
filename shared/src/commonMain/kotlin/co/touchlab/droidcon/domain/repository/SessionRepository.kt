package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Session

interface SessionRepository: Repository<Session.Id, Session>