import { Link } from "react-router-dom";
import { ArrowRight, Sparkles } from "lucide-react";
import { Button } from "@/components/ui/button";

export function Hero() {
  return (
    <section className="relative overflow-hidden">
      {/* Background gradient */}
      <div className="absolute inset-0 -z-10 bg-linear-to-b from-primary/5 via-background to-background" />
      <div className="absolute left-1/2 top-0 -z-10 h-125 w-200 -translate-x-1/2 rounded-full bg-primary/10 blur-3xl" />

      <div className="container mx-auto px-4 pt-20 pb-16 sm:pt-28 sm:pb-24">
        <div className="mx-auto max-w-3xl text-center">
          {/* Badge */}
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border bg-background px-4 py-1.5 text-sm shadow-sm">
            <Sparkles className="h-4 w-4 text-primary" />
            <span className="font-medium">New: Smart task analytics</span>
          </div>

          {/* Headline */}
          <h1 className="text-4xl font-bold tracking-tight sm:text-6xl">
            Manage tasks with{" "}
            <span className="bg-linear-to-r from-primary to-purple-600 bg-clip-text text-transparent">
              clarity and speed
            </span>
          </h1>

          {/* Subheadline */}
          <p className="mx-auto mt-6 max-w-2xl text-lg text-muted-foreground sm:text-xl">
            NexusTasks helps teams and individuals organize work, track
            progress, and hit deadlines — all in one beautiful, secure platform.
          </p>

          {/* CTAs */}
          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Button
              size="lg"
              className="w-full sm:w-auto gap-2"
              variant="default"
              render={(props) => <Link to="/register" {...props} />}
            >
              Start for free
              <ArrowRight className="h-4 w-4" />
            </Button>
            <Button
              size="lg"
              variant="outline"
              className="w-full sm:w-auto"
              render={(props) => <Link to="/login" {...props} />}
            >
              Sign in to your account
            </Button>
          </div>

          <p className="mt-4 text-sm text-muted-foreground">
            Free forever. No credit card required.
          </p>
        </div>

        {/* Hero image */}
        <div className="relative mx-auto mt-16 max-w-5xl">
          <div className="absolute -inset-4 -z-10 rounded-2xl bg-linear-to-r from-primary/20 to-purple-600/20 blur-2xl" />
          <div className="overflow-hidden rounded-xl border shadow-2xl">
            <img
              src="/images/hero-dashboard.png"
              alt="NexusTasks dashboard preview showing kanban board and analytics"
              className="w-full object-cover"
              loading="eager"
            />
          </div>
        </div>
      </div>
    </section>
  );
}
