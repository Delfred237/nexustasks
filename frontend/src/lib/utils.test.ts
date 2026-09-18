import { describe, it, expect } from "vitest";
import { cn, resolveAvatarUrl } from "./utils";

describe("cn", () => {
  it("merge plusieurs classes", () => {
    expect(cn("a", "b")).toBe("a b");
  });

  it("résout les conflits Tailwind (le dernier gagne)", () => {
    expect(cn("p-2", "p-4")).toBe("p-4");
  });

  it("ignore les valeurs falsy", () => {
    expect(cn("a", false, undefined, null, "b")).toBe("a b");
  });
});

describe("resolveAvatarUrl", () => {
  it("retourne null pour les valeurs vides", () => {
    expect(resolveAvatarUrl(null)).toBeNull();
    expect(resolveAvatarUrl(undefined)).toBeNull();
    expect(resolveAvatarUrl("")).toBeNull();
  });

  it("conserve les URLs absolues", () => {
    expect(resolveAvatarUrl("https://cdn.example.com/a.png")).toBe(
      "https://cdn.example.com/a.png",
    );
  });

  it("conserve les chemins publics déjà complets", () => {
    expect(resolveAvatarUrl("/api/files/avatars/a.png")).toBe(
      "/api/files/avatars/a.png",
    );
  });

  it("préfixe les chemins de stockage bruts", () => {
    expect(resolveAvatarUrl("avatars/a.png")).toBe("/api/files/avatars/a.png");
  });
});
