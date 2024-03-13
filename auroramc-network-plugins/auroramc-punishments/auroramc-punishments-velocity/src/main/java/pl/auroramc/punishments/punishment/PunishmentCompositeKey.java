package pl.auroramc.punishments.punishment;

record PunishmentCompositeKey(
    Long penalizedId,
    PunishmentScope scope,
    PunishmentState state
) {

}