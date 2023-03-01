package sca.web.config;

//public class AuditorAwareImpl implements AuditorAware<Tenant> {
//    @Override
//    public Optional<Tenant> getCurrentAuditor() {
//        return Optional.ofNullable(SecurityContextHolder.getContext())
//                .map(SecurityContext::getAuthentication)
//                .filter(Authentication::isAuthenticated)
//                .map(Authentication::getPrincipal)
//                .map(User.class::cast)
//                .map(User::getTenant);
//    }
//}
