package com.example.secerrordemo.domain.security.log;

import org.springframework.data.jpa.repository.JpaRepository;

interface AuthenticationLogEntryRepository extends JpaRepository<AuthenticationLogEntry, Long> {
}
