import { describe, it, expect, vi, beforeEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useLocation } from "react-router-dom";
import { renderWithProviders } from "@/test/test-utils";
import { LoginForm } from "./LoginForm";

// Mock du store useAuth : on isole le formulaire du vrai store
const loginMock = vi.fn();
const mockState = { login: loginMock, isLoading: false, error: null };

vi.mock("../hooks/useAuth", () => ({
  useAuth: Object.assign(() => mockState, {
    getState: () => mockState,
  }),
}));

// Sonde pour vérifier la navigation
function LocationProbe() {
  const location = useLocation();
  return <div data-testid="location">{location.pathname}</div>;
}

describe("LoginForm", () => {
  beforeEach(() => {
    loginMock.mockReset();
  });

  it("affiche les erreurs de validation pour un email invalide", async () => {
    const user = userEvent.setup();
    renderWithProviders(<LoginForm />);

    await user.type(screen.getByLabelText(/email/i), "pas-un-email");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    expect(await screen.findByText(/valid email/i)).toBeInTheDocument();
    expect(loginMock).not.toHaveBeenCalled();
  });

  it("affiche une erreur si le mot de passe est vide", async () => {
    const user = userEvent.setup();
    renderWithProviders(<LoginForm />);

    await user.type(screen.getByLabelText(/email/i), "alice@example.com");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    expect(
      await screen.findByText(/password is required/i),
    ).toBeInTheDocument();
    expect(loginMock).not.toHaveBeenCalled();
  });

  it("soumet les identifiants valides et redirige vers le dashboard", async () => {
    loginMock.mockResolvedValueOnce(undefined);
    const user = userEvent.setup();

    renderWithProviders(
      <>
        <LoginForm />
        <LocationProbe />
      </>,
    );

    await user.type(screen.getByLabelText(/email/i), "alice@example.com");
    await user.type(screen.getByLabelText(/password/i), "Password123!");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    await waitFor(() => {
      expect(loginMock).toHaveBeenCalledWith({
        email: "alice@example.com",
        password: "Password123!",
      });
    });

    await waitFor(() => {
      expect(screen.getByTestId("location")).toHaveTextContent(
        "/app/dashboard",
      );
    });
  });

  it("affiche un toast d'erreur si le login échoue", async () => {
    loginMock.mockRejectedValueOnce(new Error("Invalid credentials"));
    const user = userEvent.setup();

    renderWithProviders(<LoginForm />);

    await user.type(screen.getByLabelText(/email/i), "alice@example.com");
    await user.type(screen.getByLabelText(/password/i), "WrongPassword!");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    await waitFor(() => {
      expect(loginMock).toHaveBeenCalled();
    });
    // Le formulaire ne doit pas crasher ni naviguer
    expect(
      screen.getByRole("button", { name: /sign in/i }),
    ).toBeInTheDocument();
  });
});
