import { Link } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function LandingCTA() {
  return (
    <section id="pricing" className="py-20 sm:py-28">
      <div className="container mx-auto px-4">
        <div className="relative overflow-hidden rounded-2xl bg-primary px-6 py-16 sm:px-16 sm:py-20 text-center">
          {/* Decorative circles */}
          <div className="absolute -top-24 -right-24 h-64 w-64 rounded-full bg-white/10" />
          <div className="absolute -bottom-24 -left-24 h-64 w-64 rounded-full bg-white/10" />

          <div className="relative z-10">
            <h2 className="text-3xl font-bold tracking-tight text-primary-foreground sm:text-4xl">
              Ready to get more done?
            </h2>
            <p className="mx-auto mt-4 max-w-xl text-lg text-primary-foreground/80">
              Join thousands of professionals who organize their work with NexusTasks.
              Free to start, powerful from day one.
            </p>
            <div className="mt-8">
              <Button
              size="lg"
              className="gap-2"
              variant="secondary"
              render={(props) => <Link to="/register" {...props} />}
            >
              Create your free account
              <ArrowRight className="h-4 w-4" />
            </Button>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}