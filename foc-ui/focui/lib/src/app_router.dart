// lib/routing/app_router.dart
import 'package:focui/main.dart';
import 'package:focui/src/auth/login_page.dart';
import 'package:focui/src/entities/meta_feature/meta_entity_list_view.dart';
import 'package:focui/src/menu/menu_view.dart';
import 'package:focui/src/services/auth_service.dart';
import 'package:go_router/go_router.dart';

final GoRouter appRouter = GoRouter(
  initialLocation: '/',
  redirect: (context, state) async {
    final authService = getIt<AuthService>();
    final loggedIn = await authService.isLoggedIn();
    final loggingIn = state.matchedLocation == '/login';

    if (!loggedIn) {
      return loggingIn ? null : '/login';
    }

    if (loggingIn) {
      return '/home';
    }

    return null;
  },
  routes: [
    GoRoute(
      path: '/',
      redirect: (_, __) => '/home',
    ),
    GoRoute(
      path: '/login',
      name: 'login',
      builder: (context, state) => const LoginPage(),
    ),
    GoRoute(
      path: '/home',
      name: 'home',
      builder: (context, state) => const MenuView(),
    ),
    GoRoute(
      path: '/entities',
      name: 'entities',
      builder: (context, state) => const MetaEntityListView(),
    ),
  ],
);
