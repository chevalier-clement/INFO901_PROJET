# TODO

- [ ] **HandlingCsState** : Forcer la changement d'état en cas de timeout (actuellement la réception d'un message est nécessaire pour vérifier si le timeout est atteint)
- [ ] **Communicator.stop()** :: Verifications de sécurité (token, section critique) + Mise à jour des ids des autres processus (et de la table de routage)
- [ ] **Communicator** : Uniformiser les méthodes d'attente et de blocage (while + sleep / waitForAckFrom...)
