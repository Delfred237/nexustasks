import {
  LayoutDashboard,
  Shield,
  Smartphone,
  Bell,
  Search,
  Archive,
} from "lucide-react";

const features = [
  {
    icon: LayoutDashboard,
    title: "Powerful dashboards",
    description:
      "Get a real-time overview of your workload with live statistics and activity charts.",
  },
  {
    icon: Search,
    title: "Smart search & filters",
    description:
      "Find any task instantly with full-text search, status filters, and priority sorting.",
  },
  {
    icon: Bell,
    title: "Real-time notifications",
    description:
      "Stay on top of changes with in-app notifications for every important event.",
  },
  {
    icon: Archive,
    title: "Archive, never lose",
    description:
      "Archive completed work without deleting it. Restore anything, anytime.",
  },
  {
    icon: Shield,
    title: "Enterprise-grade security",
    description:
      "JWT authentication, encrypted passwords, and strict ownership checks on every resource.",
  },
  {
    icon: Smartphone,
    title: "Web & mobile ready",
    description:
      "One API powering both the React web app and the Flutter mobile app seamlessly.",
  },
];

export function Features() {
  return (
    <section id="features" className="py-20 sm:py-28 bg-muted/30">
      <div className="container mx-auto px-4">
        <div className="mx-auto max-w-2xl text-center mb-16">
          <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
            Everything you need to stay organized
          </h2>
          <p className="mt-4 text-lg text-muted-foreground">
            Built for professionals who value clarity, speed, and security.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((feature) => (
            <div
              key={feature.title}
              className="group rounded-xl border bg-background p-6 transition-all hover:shadow-lg hover:border-primary/30"
            >
              <div className="mb-4 inline-flex rounded-lg bg-primary/10 p-3 group-hover:bg-primary/20 transition-colors">
                <feature.icon className="h-6 w-6 text-primary" />
              </div>
              <h3 className="mb-2 text-lg font-semibold">{feature.title}</h3>
              <p className="text-sm text-muted-foreground leading-relaxed">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
