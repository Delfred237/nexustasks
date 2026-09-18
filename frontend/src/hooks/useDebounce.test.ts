import { describe, it, expect, vi } from "vitest";
import { renderHook, act } from "@testing-library/react";
import { useDebounce } from "./useDebounce";

describe("useDebounce", () => {
  it("retourne la valeur initiale immédiatement", () => {
    const { result } = renderHook(() => useDebounce("hello", 300));
    expect(result.current).toBe("hello");
  });

  it("ne met à jour la valeur qu'après le délai", () => {
    vi.useFakeTimers();

    const { result, rerender } = renderHook(
      ({ value }) => useDebounce(value, 300),
      {
        initialProps: { value: "a" },
      },
    );

    rerender({ value: "ab" });
    expect(result.current).toBe("a"); // Pas encore appliqué

    act(() => {
      vi.advanceTimersByTime(299);
    });
    expect(result.current).toBe("a"); // Toujours pas

    act(() => {
      vi.advanceTimersByTime(1);
    });
    expect(result.current).toBe("ab"); // Appliqué après 300ms

    vi.useRealTimers();
  });

  it("annule le timer précédent si la valeur change rapidement", () => {
    vi.useFakeTimers();

    const { result, rerender } = renderHook(
      ({ value }) => useDebounce(value, 300),
      {
        initialProps: { value: "a" },
      },
    );

    rerender({ value: "ab" });
    act(() => {
      vi.advanceTimersByTime(100);
    });
    rerender({ value: "abc" }); // Change avant la fin du délai
    act(() => {
      vi.advanceTimersByTime(100);
    });

    expect(result.current).toBe("a"); // 'ab' n'a jamais été appliqué

    act(() => {
      vi.advanceTimersByTime(200);
    });
    expect(result.current).toBe("abc"); // Seule la dernière valeur compte

    vi.useRealTimers();
  });
});
